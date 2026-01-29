import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../../services/auth-service';
import {Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  email = "";
  password = "";
  isLoading = false;
  error = "";
  successMessage = "";

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
  }

  onSubmit() {
    if (!this.email || !this.password) {
      this.error = "Please fill in all fields";
      return;
    }

    this.isLoading = true;
    this.error = "";
    this.successMessage = "";

    this.authService.login(this.email, this.password).subscribe({
      next: () => {
        this.successMessage = "Login successful! Redirecting...";
        // Keep isLoading = true to prevent double-submit during redirection delay
        setTimeout(() => {
          this.isLoading = false;
          this.router.navigate(["/collections"]);
        }, 1500);
      },
      error: (err) => {
        this.isLoading = false;
        // Extract error message from HttpErrorResponse
        this.error = err.error?.message || (typeof err.error === 'string' ? err.error : null) || "Invalid email or password";
      }
    });
  }
}
