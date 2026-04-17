import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ConflictHandlerService } from '../../../../core/http/conflict-handler.service';
import { DepartamentoResponse } from '../../../../core/models/api.types';
import {
  createRecoverableFormState,
  preserveRecoverableDraft,
  RecoverableFormState
} from '../../../../shared/forms/recoverable-form-state';
import { OperationFeedback } from '../../../../shared/models/operation-feedback.model';
import { EmpleadoFormComponent } from '../../components/empleado-form/empleado-form.component';
import {
  EmpleadoFormValue,
  EmpleadoUpdatePayload
} from '../../models/empleado.models';
import { DepartamentosApiService } from '../../services/departamentos-api.service';
import { EmpleadosApiService } from '../../services/empleados-api.service';

@Component({
  selector: 'app-empleado-edit',
  imports: [CommonModule, RouterLink, EmpleadoFormComponent],
  templateUrl: './empleado-edit.component.html',
  styleUrl: './empleado-edit.component.css'
})
export class EmpleadoEditComponent implements OnInit {
  protected loading = false;
  protected feedback: OperationFeedback | null = null;
  protected departamentoOptions: DepartamentoResponse[] = [];
  protected formState: RecoverableFormState<EmpleadoFormValue> =
    createRecoverableFormState<EmpleadoFormValue>();

  private clave = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly empleadosApiService: EmpleadosApiService,
    private readonly departamentosApiService: DepartamentosApiService,
    private readonly conflictHandlerService: ConflictHandlerService
  ) {}

  ngOnInit(): void {
    this.clave = this.route.snapshot.paramMap.get('clave') ?? '';
    if (!this.clave) {
      this.feedback = {
        type: 'error',
        message: 'No se recibio una clave valida de empleado.',
        fieldErrors: null,
        retryable: false
      };
      return;
    }

    this.loadDepartamentos();
    this.loadEmpleado();
  }

  protected submit(formValue: EmpleadoFormValue): void {
    if (this.loading || !this.clave) {
      return;
    }

    const payload: EmpleadoUpdatePayload = {
      nombre: formValue.nombre,
      direccion: formValue.direccion,
      telefono: formValue.telefono,
      departamentoClave: formValue.departamentoClave,
      version: formValue.version
    };

    this.loading = true;

    this.empleadosApiService.update(this.clave, payload).subscribe({
      next: () => {
        this.loading = false;
        this.feedback = {
          type: 'success',
          message: 'Empleado actualizado correctamente.',
          fieldErrors: null,
          retryable: false
        };
        void this.router.navigate(['/app/empleados']);
      },
      error: (error: unknown) => {
        this.loading = false;
        const mappedFeedback = this.conflictHandlerService.handle(error);
        this.feedback = mappedFeedback;
        this.formState = preserveRecoverableDraft(
          this.formState,
          formValue,
          mappedFeedback
        );
      }
    });
  }

  private loadEmpleado(): void {
    this.loading = true;

    this.empleadosApiService.get(this.clave).subscribe({
      next: (empleado) => {
        this.loading = false;
        this.formState = createRecoverableFormState<EmpleadoFormValue>({
          nombre: empleado.nombre,
          direccion: empleado.direccion,
          telefono: empleado.telefono,
          departamentoClave: empleado.departamentoClave,
          version: empleado.version
        });
      },
      error: (error: unknown) => {
        this.loading = false;
        this.feedback = this.conflictHandlerService.handle(error);
      }
    });
  }

  private loadDepartamentos(): void {
    this.departamentosApiService.list(0, 100).subscribe({
      next: (response) => {
        this.departamentoOptions = response.content;
      },
      error: () => {
        this.departamentoOptions = [];
      }
    });
  }
}
