import {
  AfterViewChecked,
  ChangeDetectorRef,
  Component,
  ElementRef,
  inject,
  NgZone,
  OnDestroy,
  OnInit,
  ViewChild,
  PLATFORM_ID
} from '@angular/core';
import { FormsModule } from '@angular/forms'; // keep if you use standalone components; otherwise import in module
import { ActivatedRoute } from '@angular/router';
import { Subscription, of } from 'rxjs';
import { catchError, switchMap, tap, timeout } from 'rxjs/operators';
import { DocumentModel } from '../../model/document-model';
import { ChatRecordModel } from '../../model/chat-record-model';
import { CollectionService } from '../../services/collection-service';
import { WebsocketService } from '../../services/websocket-service';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-collection',
  templateUrl: './collection.html',
  styleUrls: ['./collection.scss'],
  imports: [
    FormsModule
  ],
  // If you intended this to be a standalone component, add `standalone: true` and keep imports:
  // standalone: true,
  // imports: [ FormsModule ]
})
export class Collection implements OnInit, AfterViewChecked, OnDestroy {
  collectionName = "Collection";
  messageInput = "";
  private shouldScroll = true;
  private collectionId: number | null = null;

  // using inject() for services (valid)
  private collectionService = inject(CollectionService);
  private websocketService = inject(WebsocketService);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);
  private ngZone = inject(NgZone);

  private askResultSub: Subscription | null = null;
  private connectionStateSub: Subscription | null = null;
  private routeSub: Subscription | null = null;

  isLoading = true;
  loadError: string | null = null;
  wsConnected = false;

  @ViewChild("messagesContainer") messagesContainer!: ElementRef<HTMLDivElement>;
  documents: DocumentModel[] = [];
  chatMessages: ChatRecordModel[] = [];

  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    // switchMap: when cid changes, cancel previous in-flight work and start fresh.
    // Resetting state on every cid change fixes reused-component navigation (e.g. /collections/401 -> /collections/402).
    this.routeSub = this.route.paramMap.pipe(
      switchMap((params) => {
        const cid = params.get('cid');
        const parsed = cid ? parseInt(cid, 10) : NaN;
        const id = isNaN(parsed) ? null : parsed;

        if (id === null) {
          this.ngZone.run(() => {
            this.isLoading = false;
            this.loadError = cid != null ? 'Invalid collection id' : null;
            this.cdr.detectChanges();
          });
          return of(null);
        }

        this.collectionId = id;
        this.resetStateForNewCollection();

        if (!isPlatformBrowser(this.platformId)) {
          this.ngZone.run(() => {
            this.isLoading = false;
            this.cdr.detectChanges();
          });
          return of(null);
        }

        return this.collectionService.getCollectionById(id).pipe(
          timeout(15000),
          catchError((err) => {
            console.error('Error loading collection info', err);
            return of({
              id: this.collectionId!,
              name: 'Collection',
              documentCount: 0,
              description: '',
            });
          }),
          tap((collection) => {
            this.ngZone.run(() => {
              this.loadError = null;
              this.isLoading = false;
              this.collectionName = collection.name || 'Collection';
              this.loadDocumentsAndHistory();

              //TODO debug websockets
              // this.startWebSocketSession();
              // this.cdr.detectChanges();
            });
          }),
        );
      }),
    ).subscribe({
      error: (err) => {
        this.ngZone.run(() => {
          console.error('Critical error loading collection', err);
          this.isLoading = false;
          this.loadError =
            err?.name === 'TimeoutError'
              ? 'Request timed out. Check that the API is running at the configured URL.'
              : 'Failed to load collection. Check your connection and that the API is reachable.';
          this.cdr.detectChanges();
        });
      },
    });
  }

  /** Call whenever route param cid changes so the same component instance starts clean. */
  private resetStateForNewCollection(): void {
    this.askResultSub?.unsubscribe();
    this.askResultSub = null;
    this.connectionStateSub?.unsubscribe();
    this.connectionStateSub = null;
    this.websocketService.disconnect();

    this.isLoading = true;
    this.loadError = null;
    this.documents = [];
    this.chatMessages = [];
    this.collectionName = 'Collection';
    this.wsConnected = false;
    this.messageInput = '';
  }

  ngOnDestroy() {
    this.askResultSub?.unsubscribe();
    this.askResultSub = null;
    this.connectionStateSub?.unsubscribe();
    this.connectionStateSub = null;
    this.routeSub?.unsubscribe();
    this.routeSub = null;
    this.websocketService.disconnect();
  }

  private startWebSocketSession(): void {
    this.askResultSub?.unsubscribe();
    this.connectionStateSub?.unsubscribe();

    this.websocketService.connect();

    this.askResultSub = this.websocketService.askResult$.subscribe((msg) => {
      this.ngZone.run(() => {
        this.chatMessages = [...this.chatMessages, msg];
        this.shouldScroll = true;
        this.cdr.detectChanges();
      });
    });

    this.connectionStateSub = this.websocketService.connectionState$.subscribe((state) => {
      this.ngZone.run(() => {
        this.wsConnected = state === 'connected';
        this.cdr.detectChanges();
      });
    });
  }

  loadCollectionData(): void {
    console.log("ngOnInit cid");
    if (this.collectionId === null) return;
    this.isLoading = true;
    this.loadError = null;

    this.collectionService.getCollectionById(this.collectionId).pipe(
        timeout(15000),
        catchError(err => {
          console.error('Error loading collection info', err);
          return of({ id: this.collectionId!, name: 'Collection', documentCount: 0, description: '' });
        })
    ).subscribe({
      next: (collection) => {
        this.ngZone.run(() => {
          console.log("ngOnInit cid");
          this.loadError = null;
          this.isLoading = false;
          this.collectionName = collection.name || 'Collection';
          this.startWebSocketSession();
          this.loadDocumentsAndHistory();
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          console.error('Critical error loading collection', err);
          this.isLoading = false;
          this.loadError = err?.name === 'TimeoutError'
              ? 'Request timed out. Check that the API is running at the configured URL.'
              : 'Failed to load collection. Check your connection and that the API is reachable.';
          this.cdr.detectChanges();
        });
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
      this.ngZone.run(() => {
        this.documents = (Array.isArray(docs) ? docs : []).map(d => ({
          id: d.id,
          name: d.docName ?? d.fileName ?? d.name ?? 'Document',
          size: d.size ?? 'Unknown',
          uploadedAt: d.addedDate ? this.formatDate(d.addedDate) : 'Unknown',
          status: (d.status ?? 'indexed') as 'indexed' | 'processing' | 'error'
        }));
        this.cdr.detectChanges();
      });
    });

    this.collectionService.getChatHistory(this.collectionId).pipe(
        timeout(15000),
        catchError(err => {
          console.error('Error loading chat history', err);
          return of([]);
        })
    ).subscribe(history => {
      this.ngZone.run(() => {
        this.chatMessages = (Array.isArray(history) ? history : []).map(h => ({
          id: String(h.id ?? ''),
          role: (h.role === 'user' ? 'user' : 'assistant') as 'user' | 'assistant',
          content: h.message ?? h.content ?? '',
          timestamp: h.date ? this.formatDate(h.date) : (h.timestamp ?? new Date().toLocaleTimeString())
        }));
        this.shouldScroll = true;
        this.cdr.detectChanges();
      });
    });
  }

  private formatDate(value: string | Date): string {
    if (typeof value === 'string') {
      const d = new Date(value);
      return isNaN(d.getTime()) ? value : d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    return value && typeof (value as Date).toLocaleTimeString === 'function'
        ? (value as Date).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        : String(value);
  }

  ngAfterViewChecked() {
    if (this.shouldScroll) {
      this.scrollToBottom();
    }
  }

  private scrollToBottom() {
    try {
      if (this.messagesContainer && this.messagesContainer.nativeElement) {
        const element = this.messagesContainer.nativeElement;
        const isAtBottom =
            element.scrollHeight - (element.scrollTop + element.clientHeight) < 50;

        if (isAtBottom) {
          element.scrollTop = element.scrollHeight;
        }
        this.shouldScroll = false;
      }
    } catch (error) {
      // silently handle any scroll errors
      console.error('scrollToBottom error', error);
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

    this.collectionService.askQuestion(this.collectionId, { question }).subscribe({
      next: () => {
        // answer arrives via websocket subscription
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

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
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
