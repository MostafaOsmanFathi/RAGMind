import { Component, OnInit } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ProfileModel} from '../../model/profile-model';
import {AuthService} from '../../services/auth-service';
import {UserService} from '../../services/user-service';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule
  ],
  templateUrl: './settings.html',
  styleUrl: './settings.scss',
})
export class Settings implements OnInit {
  profileData: ProfileModel = {
    name: "",
    email: "",
    bio: "",
  };

  passwordData = {
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  };

  preferences = {
    emailNotifications: true,
    analytics: false,
  };

  message = "";
  isError = false;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    this.fetchUserProfile();
  }

  fetchUserProfile() {
    this.userService.getMe().subscribe({
      next: (user) => {
        this.profileData = {
          name: user.name,
          email: user.email,
          bio: user.bio || ""
        };
      },
      error: (err) => {
        this.showMessage("Failed to fetch profile info", true);
        console.error(err);
      }
    });
  }

  saveProfile() {
    this.userService.updateUser(this.profileData).subscribe({
      next: (updatedUser) => {
        this.showMessage("Profile updated successfully");
        this.authService.updateCurrentUser(updatedUser);
      },
      error: (err) => {
        this.showMessage(err.error?.message || "Failed to update profile", true);
      }
    });
  }

  changePassword() {
    if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
      this.showMessage("Passwords do not match", true);
      return;
    }

    const request = {
      oldPassword: this.passwordData.currentPassword,
      newPassword: this.passwordData.newPassword
    };

    this.userService.changePassword(request).subscribe({
      next: () => {
        this.showMessage("Password updated successfully");
        this.passwordData = {
          currentPassword: "",
          newPassword: "",
          confirmPassword: "",
        };
      },
      error: (err) => {
        this.showMessage(err.error?.message || "Failed to update password", true);
      }
    });
  }

  savePreferences() {
    // TODO: Call backend API to save preferences if endpoint exists
    this.showMessage("Preferences saved successfully");
    console.log("Preferences saved:", this.preferences);
  }

  deleteAccount() {
    if (confirm("Are you sure you want to delete your account? This action cannot be undone.")) {
      this.userService.deleteUser().subscribe({
        next: () => {
          this.authService.logout();
          this.router.navigate(["/"]);
        },
        error: (err) => {
          this.showMessage(err.error?.message || "Failed to delete account", true);
        }
      });
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(["/"]);
  }

  private showMessage(msg: string, isError: boolean = false) {
    this.message = msg;
    this.isError = isError;
    setTimeout(() => {
      this.message = "";
    }, 3000);
  }
}
