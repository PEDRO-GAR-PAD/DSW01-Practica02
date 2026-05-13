import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { SessionStoreService } from '../auth/session-store.service';
import { environment } from '../../../environments/environment';

const isApiRequest = (url: string): boolean => {
  return url.startsWith(environment.apiBaseUrl) && url.includes('/api/v1/');
};

export const authFailureInterceptor: HttpInterceptorFn = (request, next) => {
  const sessionStore = inject(SessionStoreService);
  const router = inject(Router);

  return next(request).pipe(
    catchError((error: unknown) => {
      if (
        error instanceof HttpErrorResponse &&
        error.status === 401 &&
        isApiRequest(request.url)
      ) {
        sessionStore.clearSession();
        if (router.url !== '/login') {
          void router.navigate(['/login'], {
            queryParams: {
              reason: 'expired',
              returnUrl: router.url
            }
          });
        }
      }

      return throwError(() => error);
    })
  );
};
