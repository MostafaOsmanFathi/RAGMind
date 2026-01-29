import {Injectable, inject} from '@angular/core';
import {AuthService} from './auth-service';
import {UserModel} from '../model/user-model';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {API_BASE_URL} from '../config/api-config';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private baseUrl = inject(API_BASE_URL);
  private apiUrl = `${this.baseUrl}/user`;

  private getHeaders(): HttpHeaders {
    const user = this.authService.currentUserValue;
    const token = user?.accessToken;
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getCurrentUser(): Observable<UserModel | null> {
    return this.authService.currentUser$;
  }

  getMe(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`, { headers: this.getHeaders() });
  }

  updateUser(request: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update`, request, { headers: this.getHeaders() });
  }

  changePassword(request: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/change-password`, request, { headers: this.getHeaders() });
  }

  deleteUser(): Observable<any> {
    return this.http.delete(`${this.apiUrl}/me`, { headers: this.getHeaders() });
  }
}
