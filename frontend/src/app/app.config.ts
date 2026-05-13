import {
  APP_INITIALIZER,
  ApplicationConfig,
  provideZoneChangeDetection
} from '@angular/core';
import {
  provideHttpClient,
  withInterceptors
} from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { authHeaderInterceptor } from './core/http/auth-header.interceptor';
import { authFailureInterceptor } from './core/http/auth-failure.interceptor';
import { AuthSessionService } from './features/auth/services/auth-session.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authHeaderInterceptor, authFailureInterceptor])
    ),
    {
      provide: APP_INITIALIZER,
      multi: true,
      deps: [AuthSessionService],
      useFactory: (authSessionService: AuthSessionService) => {
        return () => authSessionService.bootstrapSession();
      }
    }
  ]
};
