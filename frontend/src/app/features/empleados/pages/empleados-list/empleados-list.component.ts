import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiErrorService } from '../../../../core/http/api-error.service';
import { OperationFeedback } from '../../../../shared/models/operation-feedback.model';
import {
  EmpleadoDeleteCandidate,
  EmpleadoView
} from '../../models/empleado.models';
import { EmpleadosApiService } from '../../services/empleados-api.service';
import { EmpleadoDeleteDialogComponent } from '../../components/empleado-delete-dialog/empleado-delete-dialog.component';

@Component({
  selector: 'app-empleados-list',
  imports: [CommonModule, RouterLink, EmpleadoDeleteDialogComponent],
  templateUrl: './empleados-list.component.html',
  styleUrl: './empleados-list.component.css'
})
export class EmpleadosListComponent implements OnInit {
  protected empleados: EmpleadoView[] = [];
  protected page = 0;
  protected totalPages = 0;
  protected totalElements = 0;
  protected loading = false;
  protected deleteLoading = false;
  protected feedback: OperationFeedback | null = null;
  protected deleteTarget: EmpleadoDeleteCandidate | null = null;

  constructor(
    private readonly empleadosApiService: EmpleadosApiService,
    private readonly apiErrorService: ApiErrorService
  ) {}

  ngOnInit(): void {
    this.loadPage(0);
  }

  protected loadPage(page: number): void {
    this.loading = true;

    this.empleadosApiService.list(page).subscribe({
      next: (response) => {
        this.page = response.page;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.empleados = response.content;
        this.loading = false;
      },
      error: (error: unknown) => {
        this.loading = false;
        this.feedback = this.apiErrorService.toFeedback(
          error,
          'No se pudo cargar el listado de empleados.'
        );
      }
    });
  }

  protected retryLoad(): void {
    this.loadPage(this.page);
  }

  protected openDeleteDialog(empleado: EmpleadoView): void {
    this.deleteTarget = {
      clave: empleado.clave,
      nombre: empleado.nombre
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

    this.empleadosApiService.delete(this.deleteTarget.clave).subscribe({
      next: () => {
        this.deleteLoading = false;
        this.feedback = {
          type: 'success',
          message: 'Empleado eliminado definitivamente.',
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
              'El empleado ya no existia. Se refresco el listado para sincronizar.',
            fieldErrors: null,
            retryable: false
          };
          this.closeDeleteDialog();
          this.loadPage(this.page);
          return;
        }

        this.feedback = this.apiErrorService.toFeedback(
          error,
          'No se pudo eliminar el empleado.'
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
