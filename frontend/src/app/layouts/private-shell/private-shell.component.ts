import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthSessionService } from '../../features/auth/services/auth-session.service';

@Component({
  selector: 'app-private-shell',
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './private-shell.component.html',
  styleUrl: './private-shell.component.css'
})
export class PrivateShellComponent {
  private readonly authSessionService = inject(AuthSessionService);
  private readonly router = inject(Router);

  protected readonly session$ = this.authSessionService.session$;
  protected readonly isAdmin$ = this.authSessionService.isAdmin$;

  protected logout(): void {
    this.authSessionService.logout();
    void this.router.navigate(['/login']);
  }
}
