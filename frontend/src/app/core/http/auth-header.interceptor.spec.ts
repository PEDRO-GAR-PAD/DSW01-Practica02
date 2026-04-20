import { HttpHeaders, HttpRequest, HttpResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AuthSessionView } from '../models/api.types';
import { SessionStoreService } from '../auth/session-store.service';
import { authHeaderInterceptor } from './auth-header.interceptor';

class SessionStoreStub {
  session: AuthSessionView | null = null;

  getSession(): AuthSessionView | null {
    return this.session;
  }
}

describe('authHeaderInterceptor', () => {
  let sessionStore: SessionStoreStub;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: SessionStoreService,
          useClass: SessionStoreStub
        }
      ]
    });

    sessionStore = TestBed.inject(SessionStoreService) as unknown as SessionStoreStub;
  });

  it('should add Authorization header on API request when session exists', (done) => {
    sessionStore.session = {
      empleadoClave: 'EMP001',
      principalEmail: 'admin@test.com',
      displayName: 'Admin',
      roles: ['ROLE_ADMIN'],
      authHeader: 'Basic abc123',
      isAuthenticated: true
    };

    const request = new HttpRequest('GET', '/api/v1/empleados');

    TestBed.runInInjectionContext(() =>
      authHeaderInterceptor(request, (outgoingRequest) => {
        expect(outgoingRequest.headers.get('Authorization')).toBe('Basic abc123');
        return of(new HttpResponse({ status: 200 }));
      })
    ).subscribe(() => done());
  });

  it('should not modify request when Authorization header already exists', (done) => {
    sessionStore.session = {
      empleadoClave: 'EMP001',
      principalEmail: 'admin@test.com',
      displayName: 'Admin',
      roles: ['ROLE_ADMIN'],
      authHeader: 'Basic abc123',
      isAuthenticated: true
    };

    const request = new HttpRequest('GET', '/api/v1/empleados', {
      headers: new HttpHeaders({ Authorization: 'Bearer existing-token' })
    });

    TestBed.runInInjectionContext(() =>
      authHeaderInterceptor(request, (outgoingRequest) => {
        expect(outgoingRequest.headers.get('Authorization')).toBe('Bearer existing-token');
        return of(new HttpResponse({ status: 200 }));
      })
    ).subscribe(() => done());
  });

  it('should not add Authorization header on non-API requests', (done) => {
    sessionStore.session = {
      empleadoClave: 'EMP001',
      principalEmail: 'admin@test.com',
      displayName: 'Admin',
      roles: ['ROLE_ADMIN'],
      authHeader: 'Basic abc123',
      isAuthenticated: true
    };

    const request = new HttpRequest('GET', '/assets/logo.svg');

    TestBed.runInInjectionContext(() =>
      authHeaderInterceptor(request, (outgoingRequest) => {
        expect(outgoingRequest.headers.has('Authorization')).toBeFalse();
        return of(new HttpResponse({ status: 200 }));
      })
    ).subscribe(() => done());
  });
});
