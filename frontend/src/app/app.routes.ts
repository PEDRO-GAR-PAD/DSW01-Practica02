import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './core/auth/auth.guards';
import { PrivateShellComponent } from './layouts/private-shell/private-shell.component';

export const routes: Routes = [
	{
		path: 'login',
		loadChildren: () =>
			import('./features/auth/auth.routes').then((m) => m.AUTH_ROUTES)
	},
	{
		path: 'app',
		component: PrivateShellComponent,
		canActivate: [authGuard],
		children: [
			{
				path: '',
				pathMatch: 'full',
				redirectTo: 'dashboard'
			},
			{
				path: 'dashboard',
				loadComponent: () =>
					import(
						'./features/dashboard/pages/dashboard-home/dashboard-home.component'
					).then((m) => m.DashboardHomeComponent)
			},
			{
				path: 'empleados',
				canActivate: [adminGuard],
				loadChildren: () =>
					import('./features/empleados/empleados.routes').then(
						(m) => m.EMPLEADOS_ROUTES
					)
			}
		]
	},
	{
		path: '',
		pathMatch: 'full',
		redirectTo: 'app'
	},
	{
		path: '**',
		redirectTo: 'app'
	}
];
