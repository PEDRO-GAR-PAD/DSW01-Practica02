import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  EmpleadoCreateRequest,
  EmpleadoPageResponse,
  EmpleadoResponse,
  EmpleadoUpdateRequest
} from '../../../core/models/api.types';

@Injectable({
  providedIn: 'root'
})
export class EmpleadosApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/api/v1/empleados`;

  constructor(private readonly httpClient: HttpClient) {}

  list(page = 0): Observable<EmpleadoPageResponse> {
    return this.httpClient.get<EmpleadoPageResponse>(this.baseUrl, {
      params: {
        page
      }
    });
  }

  get(clave: string): Observable<EmpleadoResponse> {
    return this.httpClient.get<EmpleadoResponse>(`${this.baseUrl}/${clave}`);
  }

  create(payload: EmpleadoCreateRequest): Observable<EmpleadoResponse> {
    return this.httpClient.post<EmpleadoResponse>(this.baseUrl, payload);
  }

  update(clave: string, payload: EmpleadoUpdateRequest): Observable<EmpleadoResponse> {
    return this.httpClient.put<EmpleadoResponse>(`${this.baseUrl}/${clave}`, payload);
  }

  delete(clave: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${clave}`);
  }
}
