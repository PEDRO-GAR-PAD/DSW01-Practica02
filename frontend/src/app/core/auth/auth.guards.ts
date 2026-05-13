import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { SessionStoreService } from './session-store.service';
import { AuthSessionView } from '../models/api.types';

const toLoginTree = (router: Router, returnUrl: string): UrlTree => {
  return router.createUrlTree(['/login'], {
    queryParams: {
      returnUrl
    }
  });
};

const hasAdminRole = (session: AuthSessionView | null): boolean => {
  if (!session?.isAuthenticated) {
    return false;
  }

  if (!session.roles.length) {
    return ['admin', 'master'].includes(session.principalEmail.toLowerCase());
  }

  return session.roles.some((role) => {
    const normalizedRole = role.replace(/^ROLE_/, '').toUpperCase();
    return normalizedRole === 'ADMIN' || normalizedRole === 'MASTER';
  });
};

export const authGuard: CanActivateFn = (_route, state) => {
  const sessionStore = inject(SessionStoreService);
  const router = inject(Router);

  const session = sessionStore.getSession();
  if (session?.isAuthenticated) {
    return true;
  }

  return toLoginTree(router, state.url);
};

export const adminGuard: CanActivateFn = () => {
  const sessionStore = inject(SessionStoreService);
  const router = inject(Router);

  if (hasAdminRole(sessionStore.getSession())) {
    return true;
  }

  return router.createUrlTree(['/app/dashboard']);
};

export const guestOnlyGuard: CanActivateFn = () => {
  const sessionStore = inject(SessionStoreService);
  const router = inject(Router);

  const session = sessionStore.getSession();
  if (session?.isAuthenticated) {
    return router.createUrlTree(['/app/dashboard']);
  }

  return true;
};
