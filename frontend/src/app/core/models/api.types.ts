export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  fieldErrors?: Record<string, string>;
}

export interface EmpleadoAuthMeResponse {
  empleadoClave: string;
  username: string;
  nombre: string;
  authStatus: string;
  roles?: string[];
}

export interface AuthSessionView {
  empleadoClave: string;
  principalEmail: string;
  displayName: string;
  roles: string[];
  authHeader: string;
  isAuthenticated: boolean;
}

export interface EmpleadoResponse {
  clave: string;
  nombre: string;
  direccion: string;
  telefono: string;
  departamentoClave: string;
  version: number;
}

export interface EmpleadoCreateRequest {
  nombre: string;
  direccion: string;
  telefono: string;
  departamentoClave: string;
}

export interface EmpleadoUpdateRequest extends EmpleadoCreateRequest {
  version: number;
}

export interface EmpleadoPageResponse {
  content: EmpleadoResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface DepartamentoResponse {
  clave: string;
  nombre: string;
}

export interface DepartamentoPageResponse {
  content: DepartamentoResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
