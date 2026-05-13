import { Routes } from '@angular/router';

export const DEPARTAMENTOS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/departamentos-list/departamentos-list.component').then(
        (m) => m.DepartamentosListComponent
      )
  },
  {
    path: 'nuevo',
    loadComponent: () =>
      import('./pages/departamento-create/departamento-create.component').then(
        (m) => m.DepartamentoCreateComponent
      )
  },
  {
    path: ':clave/editar',
    loadComponent: () =>
      import('./pages/departamento-edit/departamento-edit.component').then(
        (m) => m.DepartamentoEditComponent
      )
  }
];