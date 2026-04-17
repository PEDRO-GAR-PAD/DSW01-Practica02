import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AuthSessionService } from '../../../auth/services/auth-session.service';

@Component({
  selector: 'app-dashboard-home',
  imports: [CommonModule],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.css'
})
export class DashboardHomeComponent {
  constructor(protected readonly authSessionService: AuthSessionService) {}
}
