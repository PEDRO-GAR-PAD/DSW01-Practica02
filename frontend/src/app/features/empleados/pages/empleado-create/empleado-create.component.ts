import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ApiErrorService } from '../../../../core/http/api-error.service';
import { DepartamentoResponse } from '../../../../core/models/api.types';
import {
  createRecoverableFormState,
  preserveRecoverableDraft,
  RecoverableFormState
} from '../../../../shared/forms/recoverable-form-state';
import { OperationFeedback } from '../../../../shared/models/operation-feedback.model';
import { EmpleadoFormComponent } from '../../components/empleado-form/empleado-form.component';
import {
  EmpleadoCreatePayload,
  EmpleadoFormValue
} from '../../models/empleado.models';
import { DepartamentosApiService } from '../../services/departamentos-api.service';
import { EmpleadosApiService } from '../../services/empleados-api.service';

@Component({
  selector: 'app-empleado-create',
  imports: [CommonModule, RouterLink, EmpleadoFormComponent],
  templateUrl: './empleado-create.component.html',
  styleUrl: './empleado-create.component.css'
})
export class EmpleadoCreateComponent implements OnInit {
  protected loading = false;
  protected feedback: OperationFeedback | null = null;
  protected departamentoOptions: DepartamentoResponse[] = [];
  protected formState: RecoverableFormState<EmpleadoFormValue> =
    createRecoverableFormState<EmpleadoFormValue>();

  constructor(
    private readonly empleadosApiService: EmpleadosApiService,
    private readonly departamentosApiService: DepartamentosApiService,
    private readonly apiErrorService: ApiErrorService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.departamentosApiService.list(0, 100).subscribe({
      next: (response) => {
        this.departamentoOptions = response.content;
      },
      error: () => {
        this.departamentoOptions = [];
      }
    });
  }

  protected submit(formValue: EmpleadoFormValue): void {
    if (this.loading) {
      return;
    }

    const payload: EmpleadoCreatePayload = {
      nombre: formValue.nombre,
      direccion: formValue.direccion,
      telefono: formValue.telefono,
      departamentoClave: formValue.departamentoClave
    };

    this.loading = true;

    this.empleadosApiService.create(payload).subscribe({
      next: () => {
        this.loading = false;
        this.feedback = {
          type: 'success',
          message: 'Empleado creado correctamente.',
          fieldErrors: null,
          retryable: false
        };
        void this.router.navigate(['/app/empleados']);
      },
      error: (error: unknown) => {
        this.loading = false;
        const mappedFeedback = this.apiErrorService.toFeedback(
          error,
          'No se pudo crear el empleado.'
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
}
