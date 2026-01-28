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
    //TODO move logic to services
    this.authService.login(this.email, this.password).subscribe({
      next: () => {
        this.router.navigate(["/collections"]);
      },
      error: (err) => {
        this.error = "Invalid email or password";
        this.isLoading = false;
      },
    });
  }
}
