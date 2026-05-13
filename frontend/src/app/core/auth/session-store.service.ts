import { Injectable } from '@angular/core';
import { AuthSessionView } from '../models/api.types';

@Injectable({
  providedIn: 'root'
})
export class SessionStoreService {
  private readonly storageKey = 'dsw01.auth.session';

  getSession(): AuthSessionView | null {
    const rawValue = sessionStorage.getItem(this.storageKey);
    if (!rawValue) {
      return null;
    }

    try {
      return JSON.parse(rawValue) as AuthSessionView;
    } catch {
      this.clearSession();
      return null;
    }
  }

  saveSession(session: AuthSessionView): void {
    sessionStorage.setItem(this.storageKey, JSON.stringify(session));
  }

  clearSession(): void {
    sessionStorage.removeItem(this.storageKey);
  }
}
