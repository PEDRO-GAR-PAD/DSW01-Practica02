import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { authGuard, adminGuard, guestOnlyGuard } from './auth.guards';
import { SessionStoreService } from './session-store.service';
import { AuthSessionView } from '../models/api.types';

class SessionStoreStub {
  session: AuthSessionView | null = null;

  getSession(): AuthSessionView | null {
    return this.session;
  }
}

describe('auth guards', () => {
  let router: Router;
  let sessionStore: SessionStoreStub;

  const authenticatedSession: AuthSessionView = {
    empleadoClave: 'EMP001',
    principalEmail: 'admin@test.com',
    displayName: 'Admin',
    roles: ['ROLE_ADMIN'],
    authHeader: 'Basic abc123',
    isAuthenticated: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: SessionStoreService,
          useClass: SessionStoreStub
        }
      ]
    });

    router = TestBed.inject(Router);
    sessionStore = TestBed.inject(SessionStoreService) as unknown as SessionStoreStub;
  });

  it('authGuard should return true when user is authenticated', () => {
    sessionStore.session = authenticatedSession;

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as never, { url: '/app/dashboard' } as never)
    );

    expect(result).toBeTrue();
  });

  it('authGuard should redirect to login when user is not authenticated', () => {
    sessionStore.session = null;

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as never, { url: '/app/dashboard' } as never)
    );

    expect(result).toEqual(
      router.createUrlTree(['/login'], { queryParams: { returnUrl: '/app/dashboard' } })
    );
  });

  it('adminGuard should allow admin users', () => {
    sessionStore.session = authenticatedSession;

    const result = TestBed.runInInjectionContext(() => adminGuard({} as never, {} as never));

    expect(result).toBeTrue();
  });

  it('adminGuard should redirect non-admin users to dashboard', () => {
    sessionStore.session = {
      ...authenticatedSession,
      roles: ['ROLE_USER']
    };

    const result = TestBed.runInInjectionContext(() => adminGuard({} as never, {} as never));

    expect(result).toEqual(router.createUrlTree(['/app/dashboard']));
  });

  it('guestOnlyGuard should redirect authenticated users to dashboard', () => {
    sessionStore.session = authenticatedSession;

    const result = TestBed.runInInjectionContext(() => guestOnlyGuard({} as never, {} as never));

    expect(result).toEqual(router.createUrlTree(['/app/dashboard']));
  });

  it('guestOnlyGuard should allow unauthenticated users', () => {
    sessionStore.session = null;

    const result = TestBed.runInInjectionContext(() => guestOnlyGuard({} as never, {} as never));

    expect(result).toBeTrue();
  });
});
