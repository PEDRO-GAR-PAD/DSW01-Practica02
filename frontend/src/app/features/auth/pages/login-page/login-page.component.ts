import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { take } from 'rxjs';
import { ApiErrorService } from '../../../../core/http/api-error.service';
import { OperationFeedback } from '../../../../shared/models/operation-feedback.model';
import {
  LoginFormState,
  LoginSubmitStatus
} from '../../models/login-form-state.model';
import { AuthSessionService } from '../../services/auth-session.service';

@Component({
  selector: 'app-login-page',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css'
})
export class LoginPageComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authSessionService = inject(AuthSessionService);
  private readonly apiErrorService = inject(ApiErrorService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected submitStatus: LoginSubmitStatus = 'idle';
  protected feedback: OperationFeedback | null = null;
  protected showPassword = false;

  protected readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  private readonly returnUrl =
    this.activatedRoute.snapshot.queryParamMap.get('returnUrl') ||
    '/app/dashboard';

  protected onSubmit(): void {
    if (this.form.invalid || this.submitStatus === 'loading') {
      this.form.markAllAsTouched();
      return;
    }

    const formValue = this.form.getRawValue();
    const requestState: LoginFormState = {
      email: formValue.email,
      password: formValue.password,
      submitStatus: 'loading',
      errorMessage: null
    };

    this.submitStatus = requestState.submitStatus;
    this.feedback = null;

    this.authSessionService
      .login(requestState.email, requestState.password)
      .pipe(take(1))
      .subscribe({
        next: () => {
          this.submitStatus = 'success';
          void this.router.navigateByUrl(this.returnUrl);
        },
        error: (error: unknown) => {
          this.submitStatus = 'error';
          this.feedback = this.apiErrorService.toFeedback(
            error,
            'No se pudo iniciar sesion. Verifica tus credenciales.'
          );
        }
      });
  }

  protected togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }
}
