import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth-service';
import { API_BASE_URL } from '../config/api-config';

function getAccessToken(): string | null {
  const auth = inject(AuthService);
  const token = auth.currentUserValue?.accessToken;
  if (token) return token;
  if (typeof window !== 'undefined' && window.localStorage) {
    try {
      const userJson = localStorage.getItem('currentUser');
      if (userJson) {
        const user = JSON.parse(userJson);
        return user?.accessToken ?? null;
      }
    } catch {
      // ignore parse errors
    }
  }
  return null;
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = getAccessToken();
  if (!token) return next(req);

  const baseUrl = inject(API_BASE_URL);
  const url = req.url;
  const isApiRequest =
    url.startsWith(baseUrl) ||
    url.startsWith('/api/') ||
    url.startsWith('/api');
  if (!isApiRequest) return next(req);

  const cloned = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` }
  });
  return next(cloned);
};
