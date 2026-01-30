import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { ThemeService } from '../../services/theme-service';

@Component({
  selector: 'app-nav',
  standalone: true,
  templateUrl: './nav.html',
  imports: [RouterLink, RouterLinkActive, AsyncPipe],
})
export class NavComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  protected theme = inject(ThemeService);

  isAuthenticated$ = this.authService.isAuthenticated$;
  currentUser$ = this.authService.currentUser$;

  navigateHome(): void {
    this.router.navigate(['/']);
  }

  toggleTheme(): void {
    this.theme.toggleTheme();
  }

  logout(): void {
    this.navigateHome()
    this.authService.logout();
  }
}
