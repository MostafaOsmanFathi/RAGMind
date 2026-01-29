import { Component, ElementRef, inject, OnInit, OnDestroy, ViewChild, PLATFORM_ID } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { DocumentModel } from '../../model/document-model';
import { ChatRecordModel } from '../../model/chat-record-model';
import { CollectionService } from '../../services/collection-service';
import { WebsocketService } from '../../services/websocket-service';
import { isPlatformBrowser } from '@angular/common';
import { catchError, of, timeout } from 'rxjs';

@Component({
  selector: 'app-collection',
  imports: [
    FormsModule
  ],
  templateUrl: './collection.html',
  styleUrl: './collection.scss',
})
export class Collection implements OnInit, OnDestroy {
  collectionName = "Collection";
  messageInput = "";
  private shouldScroll = true;
  private collectionId: number | null = null;
  private collectionService = inject(CollectionService);
  private websocketService = inject(WebsocketService);
  private platformId = inject(PLATFORM_ID);
  private askResultSub: Subscription | null = null;
  private connectionStateSub: Subscription | null = null;
  isLoading = true;
  loadError: string | null = null;
  wsConnected = false;

  @ViewChild("messagesContainer") messagesContainer!: ElementRef<HTMLDivElement>;
  documents: DocumentModel[] = [];
  chatMessages: ChatRecordModel[] = [];

  constructor(private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const cid = params.get("cid");
      if (cid) {
        this.collectionId = parseInt(cid, 10);
        if (isPlatformBrowser(this.platformId)) {
          this.loadCollectionData();
        } else {
          this.isLoading = false;
        }
      }
    });
  }

  ngOnDestroy() {
    this.askResultSub?.unsubscribe();
    this.askResultSub = null;
    this.connectionStateSub?.unsubscribe();
    this.connectionStateSub = null;
    this.websocketService.disconnect();
  }

  private startWebSocketSession(): void {
    this.askResultSub?.unsubscribe();
    this.connectionStateSub?.unsubscribe();
    this.websocketService.connect();
    this.askResultSub = this.websocketService.askResult$.subscribe((msg) => {
      this.chatMessages = [...this.chatMessages, msg];
      this.shouldScroll = true;
    });
    this.connectionStateSub = this.websocketService.connectionState$.subscribe((state) => {
      this.wsConnected = state === 'connected';
    });
  }

  loadCollectionData(): void {
    if (this.collectionId === null) return;
    this.isLoading = true;
    this.loadError = null;

    // Load collection first; show UI as soon as it completes so we never block forever on documents/history.
    this.collectionService.getCollectionById(this.collectionId).pipe(
      timeout(15000),
      catchError(err => {
        console.error('Error loading collection info', err);
        return of({ id: this.collectionId!, name: 'Collection', documentCount: 0, description: '' });
      })
    ).subscribe({
      next: (collection) => {
        this.loadError = null;
        this.isLoading = false;
        this.collectionName = collection.name || 'Collection';
        this.startWebSocketSession();
        this.loadDocumentsAndHistory();
      },
      error: (err) => {
        console.error('Critical error loading collection', err);
        this.isLoading = false;
        this.loadError = err?.name === 'TimeoutError'
          ? 'Request timed out. Check that the API is running at the configured URL.'
          : 'Failed to load collection. Check your connection and that the API is reachable.';
      }
    });
  }

  private loadDocumentsAndHistory(): void {
    if (this.collectionId === null) return;

    this.collectionService.getDocuments(this.collectionId).pipe(
      timeout(15000),
      catchError(err => {
        console.error('Error loading documents', err);
        return of([]);
      })
    ).subscribe(docs => {
      this.documents = (Array.isArray(docs) ? docs : []).map(d => ({
        id: d.id,
        name: d.docName ?? d.fileName ?? d.name ?? 'Document',
        size: d.size ?? 'Unknown',
        uploadedAt: d.addedDate ? this.formatDate(d.addedDate) : 'Unknown',
        status: (d.status ?? 'indexed') as 'indexed' | 'processing' | 'error'
      }));
    });

    this.collectionService.getChatHistory(this.collectionId).pipe(
      timeout(15000),
      catchError(err => {
        console.error('Error loading chat history', err);
        return of([]);
      })
    ).subscribe(history => {
      this.chatMessages = (Array.isArray(history) ? history : []).map(h => ({
        id: String(h.id ?? ''),
        role: (h.role === 'user' ? 'user' : 'assistant') as 'user' | 'assistant',
        content: h.message ?? h.content ?? '',
        timestamp: h.date ? this.formatDate(h.date) : (h.timestamp ?? new Date().toLocaleTimeString())
      }));
      this.shouldScroll = true;
    });
  }

  private formatDate(value: string | Date): string {
    if (typeof value === 'string') {
      const d = new Date(value);
      return isNaN(d.getTime()) ? value : d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    return value.toLocaleTimeString?.([], { hour: '2-digit', minute: '2-digit' }) ?? String(value);
  }

  ngAfterViewChecked() {
    if (this.shouldScroll) {
      this.scrollToBottom();
    }
  }

  private scrollToBottom() {
    try {
      if (this.messagesContainer) {
        const element = this.messagesContainer.nativeElement;
        // Check if user is at or near the bottom
        const isAtBottom =
          element.scrollHeight - (element.scrollTop + element.clientHeight) < 50;

        // Only auto-scroll if user is already at the bottom
        if (isAtBottom) {
          element.scrollTop = element.scrollHeight;
        }
        this.shouldScroll = false;
      }
    } catch (error) {
      // Silently handle any scroll errors
    }
  }

  sendMessage() {
    if (!this.messageInput.trim() || this.collectionId === null) return;

    const userMessage: ChatRecordModel = {
      id: "msg-" + Date.now(),
      role: "user",
      content: this.messageInput,
      timestamp: new Date().toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
      }),
    };

    this.chatMessages.push(userMessage);
    const question = this.messageInput;
    this.messageInput = "";
    this.shouldScroll = true;

    // Backend returns "Ask task sent successfully"; the AI answer arrives via WebSocket (/user/queue/ask-result).
    this.collectionService.askQuestion(this.collectionId, { question }).subscribe({
      next: () => {
        // Answer will be pushed to chatMessages by WebsocketService.askResult$ subscription.
      },
      error: (err) => {
        console.error('Error asking question', err);
        const errorMessage: ChatRecordModel = {
          id: "msg-" + Date.now(),
          role: "assistant",
          content: "Sorry, I couldn't send your question. Check your connection and try again.",
          timestamp: new Date().toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
          }),
        };
        this.chatMessages.push(errorMessage);
        this.shouldScroll = true;
      }
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file && this.collectionId !== null) {
      this.collectionService.addDocument(this.collectionId, file).subscribe({
        next: () => {
          this.loadCollectionData();
        },
        error: (err) => console.error('Error uploading document', err)
      });
    }
  }
}
