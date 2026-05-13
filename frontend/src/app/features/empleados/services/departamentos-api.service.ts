import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  DepartamentoPageResponse,
  DepartamentoResponse
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
}
