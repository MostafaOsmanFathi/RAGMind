import {Component} from '@angular/core';
import {AuthService} from '../../services/auth-service';
import {Router, RouterLink} from '@angular/router';
import {
  AbstractControl,
  FormBuilder,
  FormsModule,
  NonNullableFormBuilder,
  ReactiveFormsModule, ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';

export const passwordMatchValidator: ValidatorFn = (
  control: AbstractControl
): ValidationErrors | null => {
  const password = control.get('password');
  const confirmPassword = control.get('confirmPassword');

  return password && confirmPassword && password.value !== confirmPassword.value
    ? {passwordMismatch: true}
    : null;
};

@Component({
  selector: 'app-register',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
class Register {
  name = "";
  email = "";
  password = "";
  confirmPassword = "";
  isLoading = false;
  error = "";
  successMessage = "";

  registerForm;

  constructor(
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.registerForm = this.fb.group(
      {
        name: ['', [Validators.required, Validators.minLength(2)]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', [Validators.required]],
      },
      {validators: passwordMatchValidator}
    );
  }


  onSubmit() {
    if (!this.name || !this.email || !this.password || !this.confirmPassword) {
      this.error = "Please fill in all fields";
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.error = "Passwords do not match";
      return;
    }

    if (this.password.length < 8) {
      this.error = "Password must be at least 8 characters";
      return;
    }

    this.isLoading = true;
    this.error = "";
    this.successMessage = "";

    this.authService.register(this.name, this.email, this.password).subscribe({
      next: () => {
        this.successMessage = "Account created successfully! Logging in...";
        // Automatically log in after registration for better UX
        this.authService.login(this.email, this.password).subscribe({
          next: () => {
            this.isLoading = false;
            setTimeout(() => {
              this.router.navigate(["/collections"]);
            }, 1500);
          },
          error: () => {
            this.isLoading = false;
            // If auto-login fails, redirect to login page
            this.router.navigate(["/login"]);
          }
        });
      },
      error: (err) => {
        this.isLoading = false;
        this.error = err.error?.message || (typeof err.error === 'string' ? err.error : null) || "Failed to create account. Email may already exist.";
      },
    });
  }
}

export default Register
