import {Injectable, inject} from '@angular/core';
import {BehaviorSubject, Observable, tap, catchError, throwError, of, switchMap, map} from 'rxjs';
import {UserModel} from '../model/user-model';
import {HttpClient} from '@angular/common/http';
import {API_BASE_URL} from '../config/api-config';

const REFRESH_TOKEN_KEY = 'refreshToken';

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
          if (user.refreshToken) this.setStoredRefreshToken(user.refreshToken);
          this.currentUserSubject.next(user);
          this.isAuthenticatedSubject.next(true);
        } catch (e) {
          console.error('Failed to parse user from localStorage', e);
          localStorage.removeItem('currentUser');
          this.clearStoredRefreshToken();
        }
      }
    }
  }

  private getStoredRefreshToken(): string | null {
    if (typeof window === 'undefined' || !window.localStorage) return null;
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  private setStoredRefreshToken(token: string | null): void {
    if (typeof window === 'undefined' || !window.localStorage) return;
    if (token) localStorage.setItem(REFRESH_TOKEN_KEY, token);
    else localStorage.removeItem(REFRESH_TOKEN_KEY);
  }

  private clearStoredRefreshToken(): void {
    this.setStoredRefreshToken(null);
  }

  isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  logout(): void {
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.removeItem('currentUser');
      this.clearStoredRefreshToken();
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
          if (user.refreshToken) this.setStoredRefreshToken(user.refreshToken);
        }
        this.currentUserSubject.next(user);
        this.isAuthenticatedSubject.next(true);
      })
    );
  }

  refreshToken() {
    const user = this.currentUserSubject.value;
    const refreshToken = user?.refreshToken ?? this.getStoredRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }
    const headers = { Authorization: `Bearer ${refreshToken}` };
    return this.http.post<{ accessToken: string; refreshToken?: string }>(`${this.apiUrl}/refreshtoken`, {}, { headers }).pipe(
      tap((response) => {
        const current = this.currentUserSubject.value;
        const updated: UserModel = current
          ? { ...current, accessToken: response.accessToken, refreshToken: response.refreshToken ?? current.refreshToken ?? refreshToken }
          : { name: '', email: '', accessToken: response.accessToken, refreshToken: response.refreshToken ?? refreshToken };
        if (typeof window !== 'undefined' && window.localStorage) {
          localStorage.setItem('currentUser', JSON.stringify(updated));
          this.setStoredRefreshToken(updated.refreshToken ?? null);
        }
        this.currentUserSubject.next(updated);
      }),
      catchError(err => {
        this.logout();
        return throwError(() => err);
      })
    );
  }

  /**
   * Tries to restore session using stored refresh token (e.g. when unauthenticated in guard).
   * Returns true if a new access token was obtained and user is restored, false otherwise.
   */
  tryRefreshFromStoredToken(): Observable<boolean> {
    const refreshToken = this.getStoredRefreshToken();
    if (!refreshToken) return of(false);

    const headers = { Authorization: `Bearer ${refreshToken}` };
    return this.http
      .post<{ accessToken: string; refreshToken?: string }>(`${this.apiUrl}/refreshtoken`, {}, { headers })
      .pipe(
        switchMap((response) => {
          const newAccessToken = response.accessToken;
          const newRefreshToken = response.refreshToken ?? refreshToken;
          const meHeaders = { Authorization: `Bearer ${newAccessToken}` };
          return this.http.get<UserModel>(`${this.baseUrl}/user/me`, { headers: meHeaders }).pipe(
            tap((user) => {
              const fullUser: UserModel = { ...user, accessToken: newAccessToken, refreshToken: newRefreshToken };
              if (typeof window !== 'undefined' && window.localStorage) {
                localStorage.setItem('currentUser', JSON.stringify(fullUser));
                this.setStoredRefreshToken(newRefreshToken);
              }
              this.currentUserSubject.next(fullUser);
              this.isAuthenticatedSubject.next(true);
            }),
            map(() => true)
          );
        }),
        catchError(() => {
          this.clearStoredRefreshToken();
          return of(false);
        })
      );
  }

  refreshUserData(): Observable<UserModel> {
    const user = this.currentUserSubject.value;
    if (!user || !user.accessToken) {
      return throwError(() => new Error('No access token available'));
    }
    const headers = { Authorization: `Bearer ${user.accessToken}` };
    return this.http.get<UserModel>(`${this.baseUrl}/user/me`, { headers }).pipe(
      tap(updatedUser => {
        this.updateCurrentUser(updatedUser);
      })
    );
  }
}
