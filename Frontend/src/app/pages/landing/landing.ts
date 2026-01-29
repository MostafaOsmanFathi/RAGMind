import {Component} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../services/auth-service';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-landing',
  imports: [
    RouterLink, AsyncPipe
  ],
  templateUrl: './landing.html',
  styleUrl: './landing.scss',
})
export class Landing {
  isAuthenticated$;
  currentUser$;


  constructor(
    private router: Router,
    private authService: AuthService
  ) {
    this.isAuthenticated$ = this.authService.isAuthenticated$;
    this.currentUser$ = this.authService.currentUser$;
  }

  getStarted() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(["/collections"]);
    } else {
      this.router.navigate(["/register"]);
    }
  }

  navigateNewCollection() {
    this.router.navigate(["/collections"]);
  }
}
