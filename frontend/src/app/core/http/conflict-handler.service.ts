import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiErrorService } from './api-error.service';
import { OperationFeedback } from '../../shared/models/operation-feedback.model';

@Injectable({
  providedIn: 'root'
})
export class ConflictHandlerService {
  constructor(private readonly apiErrorService: ApiErrorService) {}

  handle(error: unknown): OperationFeedback {
    if (error instanceof HttpErrorResponse && error.status === 409) {
      const feedback = this.apiErrorService.toFeedback(
        error,
        'El registro fue modificado por otro usuario.'
      );

      return {
        ...feedback,
        retryable: true
      };
    }

    return this.apiErrorService.toFeedback(error);
  }
}
