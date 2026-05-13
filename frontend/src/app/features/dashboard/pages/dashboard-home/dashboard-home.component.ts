import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { filter, take } from 'rxjs';
import { ApiErrorService } from '../../../../core/http/api-error.service';
import {
  AuthSessionView,
  DepartamentoResponse,
  EmpleadoResponse
} from '../../../../core/models/api.types';
import { AuthSessionService } from '../../../auth/services/auth-session.service';
import { DepartamentosApiService } from '../../../empleados/services/departamentos-api.service';
import { EmpleadosApiService } from '../../../empleados/services/empleados-api.service';
import { OperationFeedback } from '../../../../shared/models/operation-feedback.model';

@Component({
  selector: 'app-dashboard-home',
  imports: [CommonModule],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.css'
})
export class DashboardHomeComponent implements OnInit {
  private readonly authSessionService = inject(AuthSessionService);
  private readonly empleadosApiService = inject(EmpleadosApiService);
  private readonly departamentosApiService = inject(DepartamentosApiService);
  private readonly apiErrorService = inject(ApiErrorService);

  protected loading = true;
  protected feedback: OperationFeedback | null = null;
  protected session: AuthSessionView | null = null;
  protected empleado: EmpleadoResponse | null = null;
  protected departamento: DepartamentoResponse | null = null;

  ngOnInit(): void {
    this.authSessionService.session$
      .pipe(
        filter((session): session is AuthSessionView => session !== null),
        take(1)
      )
      .subscribe({
        next: (session) => {
          this.session = session;
          this.loadEmpleado(session);
        },
        error: () => {
          this.loading = false;
          this.feedback = {
            type: 'error',
            message: 'No se pudo cargar tu sesion.',
            fieldErrors: null,
            retryable: false
          };
        }
      });
  }

  protected get departmentLabel(): string {
    if (this.departamento) {
      return `${this.departamento.clave} - ${this.departamento.nombre}`;
    }

    return this.empleado?.departamentoClave || 'Sin departamento';
  }

  private loadEmpleado(session: AuthSessionView): void {
    this.empleadosApiService.get(session.empleadoClave).subscribe({
      next: (empleado) => {
        this.empleado = empleado;
        this.loadDepartamento(empleado.departamentoClave);
      },
      error: (error: unknown) => {
        this.loading = false;
        this.feedback = this.apiErrorService.toFeedback(
          error,
          'No se pudo cargar tu informacion de usuario.'
        );
      }
    });
  }

  private loadDepartamento(departamentoClave: string): void {
    this.departamentosApiService.get(departamentoClave).subscribe({
      next: (departamento: DepartamentoResponse) => {
        this.departamento = departamento;
        this.loading = false;
      },
      error: () => {
        this.departamento = null;
        this.loading = false;
        this.feedback = {
          type: 'info',
          message:
            'No se pudo cargar el nombre del departamento. Se muestra la clave asignada.',
          fieldErrors: null,
          retryable: false
        };
      }
    });
  }
}
