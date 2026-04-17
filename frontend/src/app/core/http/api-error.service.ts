import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiErrorResponse } from '../models/api.types';
import { OperationFeedback } from '../../shared/models/operation-feedback.model';

@Injectable({
  providedIn: 'root'
})
export class ApiErrorService {
  toFeedback(
    error: unknown,
    fallbackMessage = 'No fue posible completar la operacion.'
  ): OperationFeedback {
    if (!(error instanceof HttpErrorResponse)) {
      return {
        type: 'error',
        message: fallbackMessage,
        fieldErrors: null,
        retryable: false
      };
    }

    const payload = this.extractPayload(error);
    const fieldErrors = payload?.fieldErrors ?? null;
    const message = payload?.message ?? fallbackMessage;

    switch (error.status) {
      case 0:
        return {
          type: 'error',
          message: 'No hay conexion con el backend. Intenta nuevamente.',
          fieldErrors,
          retryable: true
        };
      case 400:
        return {
          type: 'warning',
          message,
          fieldErrors,
          retryable: true
        };
      case 401:
        return {
          type: 'error',
          message: 'Sesion invalida o expirada. Inicia sesion de nuevo.',
          fieldErrors,
          retryable: false
        };
      case 403:
        return {
          type: 'error',
          message: 'No tienes permisos para realizar esta accion.',
          fieldErrors,
          retryable: false
        };
      case 404:
        return {
          type: 'info',
          message,
          fieldErrors,
          retryable: false
        };
      case 409:
        return {
          type: 'warning',
          message,
          fieldErrors,
          retryable: true
        };
      default:
        return {
          type: 'error',
          message,
          fieldErrors,
          retryable: error.status >= 500
        };
    }
  }

  private extractPayload(error: HttpErrorResponse): ApiErrorResponse | null {
    if (error.error && typeof error.error === 'object') {
      return error.error as ApiErrorResponse;
    }

    return null;
  }
}
