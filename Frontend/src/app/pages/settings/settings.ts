import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ProfileModel} from '../../model/profile-model';
import {AuthService} from '../../services/auth-service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-settings',
  imports: [
    FormsModule
  ],
  templateUrl: './settings.html',
  styleUrl: './settings.scss',
})
export class Settings {
  profileData: ProfileModel = {
    name: "John Doe",
    email: "john@example.com",
    bio: "A passionate developer exploring RAG technology",
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

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  saveProfile() {
    // TODO: Call backend API to save profile
    console.log("Profile saved:", this.profileData);
  }

  changePassword() {
    if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
      console.error("Passwords do not match");
      return;
    }
    // TODO: Call backend API to change password
    console.log("Password changed");
    this.passwordData = {
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    };
  }

  savePreferences() {
    // TODO: Call backend API to save preferences
    console.log("Preferences saved:", this.preferences);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(["/"]);
  }
}
