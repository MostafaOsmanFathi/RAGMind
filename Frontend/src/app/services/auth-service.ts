import {Injectable, inject} from '@angular/core';
import {BehaviorSubject, Observable, tap, catchError, throwError} from 'rxjs';
import {UserModel} from '../model/user-model';
import {HttpClient} from '@angular/common/http';
import {API_BASE_URL} from '../config/api-config';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private baseUrl = inject(API_BASE_URL);
  private apiUrl = `${this.baseUrl}/auth`;

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private currentUserSubject = new BehaviorSubject<UserModel | null>(null);

  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  public currentUser$ = this.currentUserSubject.asObservable();

  get currentUserValue(): UserModel | null {
    return this.currentUserSubject.value;
  }

  constructor() {
    this.checkLocalStorage();
  }

  private checkLocalStorage(): void {
    if (typeof window !== 'undefined' && window.localStorage) {
      const userJson = localStorage.getItem('currentUser');
      if (userJson) {
        try {
          const user = JSON.parse(userJson);
          this.currentUserSubject.next(user);
          this.isAuthenticatedSubject.next(true);
        } catch (e) {
          console.error('Failed to parse user from localStorage', e);
          localStorage.removeItem('currentUser');
        }
      }
    }
  }

  isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  logout(): void {
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.removeItem('currentUser');
    }
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
  }

  updateCurrentUser(user: UserModel): void {
    const current = this.currentUserSubject.value;
    const updated = { ...current, ...user };
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.setItem('currentUser', JSON.stringify(updated));
    }
    this.currentUserSubject.next(updated as UserModel);
  }

  register(name: string, email: string, password: string, phoneNumber: string = '') {
    return this.http.post(`${this.apiUrl}/signup`, { name, email, password, phoneNumber });
  }

  login(email: string, password: string) {
    return this.http.post<UserModel>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap(user => {
        if (typeof window !== 'undefined' && window.localStorage) {
          localStorage.setItem('currentUser', JSON.stringify(user));
        }
        this.currentUserSubject.next(user);
        this.isAuthenticatedSubject.next(true);
      })
    );
  }

  refreshToken() {
    const user = this.currentUserSubject.value;
    if (!user || !user.refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }
    const headers = { Authorization: `Bearer ${user.refreshToken}` };
    return this.http.post<any>(`${this.apiUrl}/refreshtoken`, {}, { headers }).pipe(
      tap((response: any) => {
        if (user) {
          user.accessToken = response.accessToken;
          if (typeof window !== 'undefined' && window.localStorage) {
            localStorage.setItem('currentUser', JSON.stringify(user));
          }
          this.currentUserSubject.next(user);
        }
      }),
      catchError(err => {
        this.logout();
        return throwError(() => err);
      })
    );
  }
}
