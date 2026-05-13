import {
  DepartamentoCreateRequest,
  DepartamentoResponse,
  DepartamentoUpdateRequest
} from '../../../core/models/api.types';

export interface DepartamentoView extends DepartamentoResponse {}

export interface DepartamentoFormValue {
  nombre: string;
}

export interface DepartamentoDeleteCandidate {
  clave: string;
  nombre: string;
}

export type DepartamentoCreatePayload = DepartamentoCreateRequest;
export type DepartamentoUpdatePayload = DepartamentoUpdateRequest;