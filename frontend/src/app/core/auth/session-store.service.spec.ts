import { TestBed } from '@angular/core/testing';
import { SessionStoreService } from './session-store.service';
import { AuthSessionView } from '../models/api.types';

describe('SessionStoreService', () => {
  let service: SessionStoreService;

  const sessionMock: AuthSessionView = {
    empleadoClave: 'EMP001',
    principalEmail: 'admin@test.com',
    displayName: 'Admin Test',
    roles: ['ROLE_ADMIN'],
    authHeader: 'Basic abc123',
    isAuthenticated: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionStoreService);
    sessionStorage.clear();
  });

  afterEach(() => {
    sessionStorage.clear();
  });

  it('should save and get session', () => {
    service.saveSession(sessionMock);

    expect(service.getSession()).toEqual(sessionMock);
  });

  it('should return null and clear invalid session payload', () => {
    sessionStorage.setItem('dsw01.auth.session', '{invalid-json');

    expect(service.getSession()).toBeNull();
    expect(sessionStorage.getItem('dsw01.auth.session')).toBeNull();
  });

  it('should clear session', () => {
    service.saveSession(sessionMock);

    service.clearSession();

    expect(service.getSession()).toBeNull();
  });
});
