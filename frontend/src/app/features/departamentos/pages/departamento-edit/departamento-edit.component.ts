import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiErrorService } from '../../../../core/http/api-error.service';
import {
  createRecoverableFormState,
  preserveRecoverableDraft,
  RecoverableFormState
} from '../../../../shared/forms/recoverable-form-state';
import { OperationFeedback } from '../../../../shared/models/operation-feedback.model';
import {
  DepartamentoFormValue,
  DepartamentoUpdatePayload
} from '../../models/departamento.models';
import { DepartamentoFormComponent } from '../../components/departamento-form/departamento-form.component';
import { DepartamentosApiService } from '../../services/departamentos-api.service';

@Component({
  selector: 'app-departamento-edit',
  imports: [CommonModule, RouterLink, DepartamentoFormComponent],
  templateUrl: './departamento-edit.component.html',
  styleUrl: './departamento-edit.component.css'
})
export class DepartamentoEditComponent implements OnInit {
  protected loading = false;
  protected feedback: OperationFeedback | null = null;
  protected formState: RecoverableFormState<DepartamentoFormValue> =
    createRecoverableFormState<DepartamentoFormValue>();

  constructor(
    private readonly departamentosApiService: DepartamentosApiService,
    private readonly apiErrorService: ApiErrorService,
    private readonly activatedRoute: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    const clave = this.activatedRoute.snapshot.paramMap.get('clave');

    if (!clave) {
      this.feedback = {
        type: 'error',
        message: 'No se recibio una clave valida de departamento.',
        fieldErrors: null,
        retryable: false
      };
      return;
    }

    this.loadDepartamento(clave);
  }

  protected submit(formValue: DepartamentoFormValue): void {
    const clave = this.activatedRoute.snapshot.paramMap.get('clave');

    if (!clave || this.loading) {
      return;
    }

    const payload: DepartamentoUpdatePayload = {
      nombre: formValue.nombre
    };

    this.loading = true;

    this.departamentosApiService.update(clave, payload).subscribe({
      next: () => {
        this.loading = false;
        this.feedback = {
          type: 'success',
          message: 'Departamento actualizado correctamente.',
          fieldErrors: null,
          retryable: false
        };
        void this.router.navigate(['/app/departamentos']);
      },
      error: (error: unknown) => {
        this.loading = false;
        const mappedFeedback = this.apiErrorService.toFeedback(
          error,
          'No se pudo actualizar el departamento.'
        );
        this.feedback = mappedFeedback;
        this.formState = preserveRecoverableDraft(
          this.formState,
          formValue,
          mappedFeedback
        );
      }
    });
  }

  private loadDepartamento(clave: string): void {
    this.loading = true;

    this.departamentosApiService.get(clave).subscribe({
      next: (departamento) => {
        this.loading = false;
        this.formState = createRecoverableFormState<DepartamentoFormValue>({
          nombre: departamento.nombre
        });
      },
      error: (error: unknown) => {
        this.loading = false;
        this.feedback = this.apiErrorService.toFeedback(
          error,
          'No se pudo cargar el departamento.'
        );
      }
    });
  }
}