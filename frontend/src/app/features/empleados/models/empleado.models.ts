import {
  EmpleadoCreateRequest,
  EmpleadoPageResponse,
  EmpleadoResponse,
  EmpleadoUpdateRequest
} from '../../../core/models/api.types';

export interface EmpleadoView extends EmpleadoResponse {}

export interface EmpleadoPageView extends EmpleadoPageResponse {}

export interface EmpleadoFormValue {
  nombre: string;
  direccion: string;
  telefono: string;
  departamentoClave: string;
  version: number;
}

export interface EmpleadoDeleteCandidate {
  clave: string;
  nombre: string;
}

export type EmpleadoCreatePayload = EmpleadoCreateRequest;
export type EmpleadoUpdatePayload = EmpleadoUpdateRequest;
