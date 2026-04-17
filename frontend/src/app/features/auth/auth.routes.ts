import { Routes } from '@angular/router';
import { guestOnlyGuard } from '../../core/auth/auth.guards';

export const AUTH_ROUTES: Routes = [
  {
    path: '',
    canActivate: [guestOnlyGuard],
    loadComponent: () =>
      import('./pages/login-page/login-page.component').then(
        (m) => m.LoginPageComponent
      )
  }
];
