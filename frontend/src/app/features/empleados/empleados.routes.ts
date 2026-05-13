import { Routes } from '@angular/router';

export const EMPLEADOS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/empleados-list/empleados-list.component').then(
        (m) => m.EmpleadosListComponent
      )
  },
  {
    path: 'nuevo',
    loadComponent: () =>
      import('./pages/empleado-create/empleado-create.component').then(
        (m) => m.EmpleadoCreateComponent
      )
  },
  {
    path: ':clave/editar',
    loadComponent: () =>
      import('./pages/empleado-edit/empleado-edit.component').then(
        (m) => m.EmpleadoEditComponent
      )
  }
];
