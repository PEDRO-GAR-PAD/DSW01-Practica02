import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, firstValueFrom, map, Observable } from 'rxjs';
import {
  AuthSessionView,
  EmpleadoAuthMeResponse
} from '../../../core/models/api.types';
import { AuthApiService } from '../../../core/auth/auth-api.service';
import { SessionStoreService } from '../../../core/auth/session-store.service';

@Injectable({
  providedIn: 'root'
})
export class AuthSessionService {
  private readonly authApiService = inject(AuthApiService);
  private readonly sessionStoreService = inject(SessionStoreService);

  private readonly sessionState = new BehaviorSubject<AuthSessionView | null>(
    this.sessionStoreService.getSession()
  );

  readonly session$ = this.sessionState.asObservable();
  readonly isAuthenticated$ = this.session$.pipe(
    map((session) => Boolean(session?.isAuthenticated))
  );
  readonly isAdmin$ = this.session$.pipe(
    map((session) => this.hasAdminRole(session))
  );
  get snapshot(): AuthSessionView | null {
    return this.sessionState.value;
  }

  async bootstrapSession(): Promise<void> {
    const storedSession = this.sessionStoreService.getSession();
    if (!storedSession?.authHeader) {
      return;
    }

    try {
      const authMeResponse = await firstValueFrom(
        this.authApiService.getMe(storedSession.authHeader)
      );
      this.persistSession(
        storedSession.authHeader,
        authMeResponse,
        storedSession.principalEmail
      );
    } catch {
      this.logout();
    }
  }

  login(email: string, password: string): Observable<AuthSessionView> {
    const authHeader = this.buildBasicAuthHeader(email, password);

    return this.authApiService.getMe(authHeader).pipe(
      map((authMeResponse) => {
        return this.persistSession(authHeader, authMeResponse, email);
      })
    );
  }

  logout(): void {
    this.sessionStoreService.clearSession();
    this.sessionState.next(null);
  }

  private persistSession(
    authHeader: string,
    authMeResponse: EmpleadoAuthMeResponse,
    fallbackEmail: string
  ): AuthSessionView {
    const principalEmail = authMeResponse.username?.trim() || fallbackEmail;
    const resolvedRoles = this.resolveRoles(authMeResponse.roles, principalEmail);

    const session: AuthSessionView = {
      empleadoClave: authMeResponse.empleadoClave,
      principalEmail,
      displayName: authMeResponse.nombre,
      roles: resolvedRoles,
      authHeader,
      isAuthenticated: true
    };

    this.sessionStoreService.saveSession(session);
    this.sessionState.next(session);

    return session;
  }

  private resolveRoles(roles: string[] | undefined, principalEmail: string): string[] {
    if (Array.isArray(roles) && roles.length > 0) {
      return roles;
    }

    if (['admin', 'master'].includes(principalEmail.toLowerCase())) {
      return ['ADMIN'];
    }

    return [];
  }

  private hasAdminRole(session: AuthSessionView | null): boolean {
    if (!session?.isAuthenticated) {
      return false;
    }

    return session.roles.some((role) => {
      const normalizedRole = role.replace(/^ROLE_/, '').toUpperCase();
      return normalizedRole === 'ADMIN' || normalizedRole === 'MASTER';
    });
  }

  private buildBasicAuthHeader(email: string, password: string): string {
    return `Basic ${btoa(`${email}:${password}`)}`;
  }
}
