import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Subject, Observable } from 'rxjs';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { AuthService } from './auth-service';
import { WS_BASE_URL } from '../config/api-config';
import { ChatRecordModel } from '../model/chat-record-model';

/** Payload shape for messages received on /user/queue/ask-result (backend-dependent). */
export interface AskResultPayload {
  id?: string;
  content?: string;
  answer?: string;
  [key: string]: unknown;
}

@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
  private authService = inject(AuthService);
  private wsBaseUrl = inject(WS_BASE_URL);
  private platformId = inject(PLATFORM_ID);

  private client: Client | null = null;
  private askResultSubject = new Subject<ChatRecordModel>();
  private connectionStateSubject = new Subject<'connected' | 'disconnected' | 'error'>();
  private askResultSubscription: { unsubscribe: () => void } | null = null;

  /** Emits each AI answer as it arrives on /user/queue/ask-result. */
  get askResult$(): Observable<ChatRecordModel> {
    return this.askResultSubject.asObservable();
  }

  get connectionState$(): Observable<'connected' | 'disconnected' | 'error'> {
    return this.connectionStateSubject.asObservable();
  }

  get isConnected(): boolean {
    return this.client?.active ?? false;
  }

  connect(): void {
    if (!isPlatformBrowser(this.platformId)) return;

    const user = this.authService.currentUserValue;
    const token = user?.accessToken;
    if (!token) {
      console.warn('[WebSocket] No access token, skipping connect');
      this.connectionStateSubject.next('error');
      return;
    }

    if (this.client?.active) {
      this.connectionStateSubject.next('connected');
      return;
    }

    const socket = new SockJS(this.wsBaseUrl);
    this.client = new Client({
      webSocketFactory: () => socket,
      debug: (msg) => console.debug('[STOMP]', msg),
      reconnectDelay: 5000,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });

    this.client.onConnect = () => {
      this.connectionStateSubject.next('connected');
      this.askResultSubscription = this.client!.subscribe(
        '/user/queue/ask-result',
        (message) => {
          try {
            const body = message.body ? JSON.parse(message.body) : {};
            const record = this.mapAskResultToChatRecord(body);
            if (record) this.askResultSubject.next(record);
          } catch (e) {
            console.error('[WebSocket] Failed to parse ask-result', e);
          }
        }
      );
    };

    this.client.onStompError = () => {
      this.connectionStateSubject.next('error');
    };

    this.client.onDisconnect = () => {
      this.connectionStateSubject.next('disconnected');
      this.askResultSubscription = null;
    };

    this.client.activate();
  }

  disconnect(): void {
    if (this.askResultSubscription) {
      this.askResultSubscription.unsubscribe();
      this.askResultSubscription = null;
    }
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
    this.connectionStateSubject.next('disconnected');
  }

  private mapAskResultToChatRecord(payload: AskResultPayload): ChatRecordModel | null {
    const content =
      payload.content ?? payload.answer ?? (typeof payload === 'string' ? payload : null);
    if (content == null) return null;
    return {
      id: payload.id ?? `msg-${Date.now()}`,
      role: 'assistant',
      content: String(content),
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };
  }
}
