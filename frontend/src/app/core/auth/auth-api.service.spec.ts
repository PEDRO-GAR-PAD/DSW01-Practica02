import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuthApiService } from './auth-api.service';
import { EmpleadoAuthMeResponse } from '../models/api.types';

describe('AuthApiService', () => {
  let service: AuthApiService;
  let httpMock: HttpTestingController;

  const responseMock: EmpleadoAuthMeResponse = {
    empleadoClave: 'EMP001',
    username: 'admin@test.com',
    nombre: 'Admin',
    authStatus: 'AUTHENTICATED',
    roles: ['ROLE_ADMIN']
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AuthApiService, provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(AuthApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call auth me endpoint without auth header when not provided', () => {
    service.getMe().subscribe((response) => {
      expect(response).toEqual(responseMock);
    });

    const req = httpMock.expectOne('/api/v1/auth/empleado/me');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush(responseMock);
  });

  it('should call auth me endpoint with auth header when provided', () => {
    service.getMe('Basic abc123').subscribe((response) => {
      expect(response).toEqual(responseMock);
    });

    const req = httpMock.expectOne('/api/v1/auth/empleado/me');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Basic abc123');
    req.flush(responseMock);
  });
});
