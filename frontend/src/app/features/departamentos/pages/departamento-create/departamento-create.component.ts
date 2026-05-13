import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ApiErrorService } from '../../../../core/http/api-error.service';
import { OperationFeedback } from '../../../../shared/models/operation-feedback.model';
import {
  DepartamentoCreatePayload,
  DepartamentoFormValue
} from '../../models/departamento.models';
import { DepartamentoFormComponent } from '../../components/departamento-form/departamento-form.component';
import { DepartamentosApiService } from '../../services/departamentos-api.service';

@Component({
  selector: 'app-departamento-create',
  imports: [CommonModule, RouterLink, DepartamentoFormComponent],
  templateUrl: './departamento-create.component.html',
  styleUrl: './departamento-create.component.css'
})
export class DepartamentoCreateComponent {
  protected loading = false;
  protected feedback: OperationFeedback | null = null;

  constructor(
    private readonly departamentosApiService: DepartamentosApiService,
    private readonly apiErrorService: ApiErrorService,
    private readonly router: Router
  ) {}

  protected submit(formValue: DepartamentoFormValue): void {
    if (this.loading) {
      return;
    }

    const payload: DepartamentoCreatePayload = {
      nombre: formValue.nombre
    };

    this.loading = true;

    this.departamentosApiService.create(payload).subscribe({
      next: () => {
        this.loading = false;
        this.feedback = {
          type: 'success',
          message: 'Departamento creado correctamente.',
          fieldErrors: null,
          retryable: false
        };
        void this.router.navigate(['/app/departamentos']);
      },
      error: (error: unknown) => {
        this.loading = false;
        this.feedback = this.apiErrorService.toFeedback(
          error,
          'No se pudo crear el departamento.'
        );
      }
    });
  }
}