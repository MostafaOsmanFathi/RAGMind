import { inject, InjectionToken } from '@angular/core';
import { environment } from '../../environments/environment';

/** Base URL for the RAG backend API (e.g. http://localhost:8080 or /api when behind proxy). */
export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL', {
  providedIn: 'root',
  factory: () => environment.apiUrl,
});

/** Base URL for WebSocket endpoint (e.g. http://localhost:8080 → ws://localhost:8080/ws). */
export const WS_BASE_URL = new InjectionToken<string>('WS_BASE_URL', {
  providedIn: 'root',
  factory: () => {
    const api = inject(API_BASE_URL);
    const u = new URL(api);
    u.protocol = u.protocol === 'https:' ? 'wss:' : 'ws:';
    u.pathname = (u.pathname === '/' ? '' : u.pathname) + '/ws';
    return u.toString();
  },
});
