import {Component} from '@angular/core';
import {AsyncPipe} from '@angular/common';
import {RouterLink, RouterLinkActive, Router} from '@angular/router';
import {AuthService} from '../../services/auth-service';

@Component({
  selector: 'app-nav',
  standalone: true,
  templateUrl: './nav.html',
  imports: [
    RouterLink, RouterLinkActive, AsyncPipe
  ],
})
export class NavComponent {

  isAuthenticated$;
  currentUser$;

  constructor(private authService: AuthService, private router: Router) {
    this.isAuthenticated$ = this.authService.isAuthenticated$;
    this.currentUser$ = this.authService.currentUser$;
  }

  navigateHome() {
    this.router.navigate(["/"]);
  }

  logout() {
    this.navigateHome()
    this.authService.logout();
  }
}
