import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  DepartamentoCreateRequest,
  DepartamentoPageResponse,
  DepartamentoResponse,
  DepartamentoUpdateRequest
} from '../../../core/models/api.types';

@Injectable({
  providedIn: 'root'
})
export class DepartamentosApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/api/v1/departamentos`;

  constructor(private readonly httpClient: HttpClient) {}

  list(page = 0, size = 50): Observable<DepartamentoPageResponse> {
    return this.httpClient.get<DepartamentoPageResponse>(this.baseUrl, {
      params: {
        page,
        size
      }
    });
  }

  get(clave: string): Observable<DepartamentoResponse> {
    return this.httpClient.get<DepartamentoResponse>(`${this.baseUrl}/${clave}`);
  }

  create(payload: DepartamentoCreateRequest): Observable<DepartamentoResponse> {
    return this.httpClient.post<DepartamentoResponse>(this.baseUrl, payload);
  }

  update(
    clave: string,
    payload: DepartamentoUpdateRequest
  ): Observable<DepartamentoResponse> {
    return this.httpClient.put<DepartamentoResponse>(`${this.baseUrl}/${clave}`, payload);
  }

  delete(clave: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${clave}`);
  }
}