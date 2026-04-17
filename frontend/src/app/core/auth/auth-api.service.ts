import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EmpleadoAuthMeResponse } from '../models/api.types';

@Injectable({
  providedIn: 'root'
})
export class AuthApiService {
  private readonly authMeUrl = `${environment.apiBaseUrl}/api/v1/auth/empleado/me`;

  constructor(private readonly httpClient: HttpClient) {}

  getMe(authHeader?: string): Observable<EmpleadoAuthMeResponse> {
    const headers = authHeader
      ? new HttpHeaders({ Authorization: authHeader })
      : undefined;

    return this.httpClient.get<EmpleadoAuthMeResponse>(this.authMeUrl, {
      headers
    });
  }
}
