import { Component, signal, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {NavComponent} from './components/nav/nav';
import {Footer} from './components/footer/footer';
import {AuthService} from './services/auth-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavComponent, Footer],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('frontend');
  private authService = inject(AuthService);

  constructor() {
    if (this.authService.isAuthenticated()) {
      this.authService.refreshUserData().subscribe({
        error: (err) => console.error('Failed to refresh user data', err)
      });
    }
  }
}
