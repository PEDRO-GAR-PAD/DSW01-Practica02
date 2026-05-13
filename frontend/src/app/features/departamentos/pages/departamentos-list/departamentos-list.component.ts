import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiErrorService } from '../../../../core/http/api-error.service';
import { OperationFeedback } from '../../../../shared/models/operation-feedback.model';
import {
  DepartamentoDeleteCandidate,
  DepartamentoView
} from '../../models/departamento.models';
import { DepartamentosApiService } from '../../services/departamentos-api.service';

@Component({
  selector: 'app-departamentos-list',
  imports: [CommonModule, RouterLink],
  templateUrl: './departamentos-list.component.html',
  styleUrl: './departamentos-list.component.css'
})
export class DepartamentosListComponent implements OnInit {
  protected departamentos: DepartamentoView[] = [];
  protected page = 0;
  protected totalPages = 0;
  protected totalElements = 0;
  protected loading = false;
  protected deleteLoading = false;
  protected feedback: OperationFeedback | null = null;
  protected deleteTarget: DepartamentoDeleteCandidate | null = null;

  constructor(
    private readonly departamentosApiService: DepartamentosApiService,
    private readonly apiErrorService: ApiErrorService
  ) {}

  ngOnInit(): void {
    this.loadPage(0);
  }

  protected loadPage(page: number): void {
    this.loading = true;

    this.departamentosApiService.list(page).subscribe({
      next: (response) => {
        this.page = response.page;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.departamentos = response.content;
        this.loading = false;
      },
      error: (error: unknown) => {
        this.loading = false;
        this.feedback = this.apiErrorService.toFeedback(
          error,
          'No se pudo cargar el listado de departamentos.'
        );
      }
    });
  }

  protected retryLoad(): void {
    this.loadPage(this.page);
  }

  protected openDeleteDialog(departamento: DepartamentoView): void {
    this.deleteTarget = {
      clave: departamento.clave,
      nombre: departamento.nombre
    };
  }

  protected closeDeleteDialog(): void {
    this.deleteTarget = null;
  }

  protected confirmDelete(): void {
    if (!this.deleteTarget || this.deleteLoading) {
      return;
    }

    this.deleteLoading = true;

    this.departamentosApiService.delete(this.deleteTarget.clave).subscribe({
      next: () => {
        this.deleteLoading = false;
        this.feedback = {
          type: 'success',
          message: 'Departamento eliminado correctamente.',
          fieldErrors: null,
          retryable: false
        };
        this.closeDeleteDialog();
        this.loadPage(this.page);
      },
      error: (error: unknown) => {
        this.deleteLoading = false;

        if (error instanceof HttpErrorResponse && error.status === 404) {
          this.feedback = {
            type: 'info',
            message:
              'El departamento ya no existia. Se refresco el listado para sincronizar.',
            fieldErrors: null,
            retryable: false
          };
          this.closeDeleteDialog();
          this.loadPage(this.page);
          return;
        }

        this.feedback = this.apiErrorService.toFeedback(
          error,
          'No se pudo eliminar el departamento.'
        );
      }
    });
  }

  protected canGoPrev(): boolean {
    return this.page > 0;
  }

  protected canGoNext(): boolean {
    return this.page + 1 < this.totalPages;
  }
}