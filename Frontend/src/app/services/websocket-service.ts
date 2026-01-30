import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Subject, Observable } from 'rxjs';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { AuthService } from './auth-service';
import { WS_BASE_URL } from '../config/api-config';
import { ChatRecordModel } from '../model/chat-record-model';

/** Payload from backend /user/queue/ask-result (RagFeedbackResponseDto). collection_name is the collection id. */
export interface RagFeedbackResponsePayload {
  backend_id?: string;
  user_id?: string;
  status?: string;
  llm_model?: string;
  /** Collection id (e.g. "552" for /collections/552). */
  collection_name?: string;
  /** The AI response text. */
  response?: string;
  taskId?: string;
  [key: string]: unknown;
}

/** Emitted so the collection page can filter by collection id and append the message. */
export interface AskResultMessage {
  collectionId: number;
  record: ChatRecordModel;
}

@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
  private authService = inject(AuthService);
  private wsBaseUrl = inject(WS_BASE_URL);
  private platformId = inject(PLATFORM_ID);

  private client: Client | null = null;
  private askResultSubject = new Subject<AskResultMessage>();
  private connectionStateSubject = new Subject<'connected' | 'disconnected' | 'error'>();
  private askResultSubscription: { unsubscribe: () => void } | null = null;

  /** Emits each AI answer with its collection id so the collection page can filter and append. */
  get askResult$(): Observable<AskResultMessage> {
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

    const wsUrl = this.wsBaseUrl;
    console.debug('[WebSocket] Connecting to', wsUrl);
    const socket = new SockJS(wsUrl);
    this.client = new Client({
      webSocketFactory: () => socket,
      debug: (msg) => console.debug('[STOMP]', msg),
      reconnectDelay: 5000,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });

    this.client.onConnect = () => {
      console.debug('[WebSocket] STOMP connected');
      this.connectionStateSubject.next('connected');
      this.askResultSubscription = this.client!.subscribe(
        '/user/queue/ask-result',
        (message) => {
          try {
            const body: RagFeedbackResponsePayload = message.body ? JSON.parse(message.body) : {};
            const out = this.mapRagFeedbackToAskResult(body);
            if (out) this.askResultSubject.next(out);
          } catch (e) {
            console.error('[WebSocket] Failed to parse ask-result', e);
          }
        }
      );
    };

    this.client.onStompError = (frame: { headers?: { message?: string }; toString?: () => string }) => {
      console.error('[WebSocket] STOMP error', frame.headers?.message ?? frame.toString?.() ?? frame);
      this.connectionStateSubject.next('error');
    };

    this.client.onWebSocketClose = (ev: CloseEvent) => {
      console.warn('[WebSocket] Socket closed', ev.code, ev.reason || '');
    };

    this.client.onWebSocketError = (ev: Event) => {
      console.error('[WebSocket] Socket error', ev);
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

  private mapRagFeedbackToAskResult(payload: RagFeedbackResponsePayload): AskResultMessage | null {
    const content = payload.response ?? payload['content'] ?? payload['answer'] ?? (typeof payload === 'string' ? payload : null);
    if (content == null) return null;
    const rawId = payload.collection_name;
    const collectionId = rawId != null ? parseInt(String(rawId), 10) : NaN;
    if (isNaN(collectionId)) return null;
    const record: ChatRecordModel = {
      id: payload.taskId ?? payload.backend_id ?? `msg-${Date.now()}`,
      role: 'assistant',
      content: String(content),
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };
    return { collectionId, record };
  }
}
