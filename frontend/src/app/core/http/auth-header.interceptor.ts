import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { SessionStoreService } from '../auth/session-store.service';
import { environment } from '../../../environments/environment';

const isApiRequest = (url: string): boolean => {
  return url.startsWith(environment.apiBaseUrl) && url.includes('/api/v1/');
};

export const authHeaderInterceptor: HttpInterceptorFn = (request, next) => {
  if (!isApiRequest(request.url) || request.headers.has('Authorization')) {
    return next(request);
  }

  const sessionStore = inject(SessionStoreService);
  const session = sessionStore.getSession();
  if (!session?.authHeader) {
    return next(request);
  }

  const requestWithAuth = request.clone({
    setHeaders: {
      Authorization: session.authHeader
    }
  });

  return next(requestWithAuth);
};
