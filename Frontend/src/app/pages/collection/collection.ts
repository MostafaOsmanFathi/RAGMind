import {Component, ElementRef, inject, OnInit, ViewChild, PLATFORM_ID} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {DocumentModel} from '../../model/document-model';
import {ChatRecordModel} from '../../model/chat-record-model';
import {CollectionService} from '../../services/collection-service';
import {isPlatformBrowser} from '@angular/common';
import {catchError, forkJoin, of, finalize} from 'rxjs';

@Component({
  selector: 'app-collection',
  imports: [
    FormsModule
  ],
  templateUrl: './collection.html',
  styleUrl: './collection.scss',
})
export class Collection implements OnInit {
  collectionName = "Collection";
  messageInput = "";
  private shouldScroll = true;
  private collectionId: number | null = null;
  private collectionService = inject(CollectionService);
  private platformId = inject(PLATFORM_ID);
  isLoading = true;

  @ViewChild("messagesContainer") messagesContainer!: ElementRef<HTMLDivElement>;
  documents: DocumentModel[] = [];
  chatMessages: ChatRecordModel[] = []

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

  private loadCollectionData() {
    if (this.collectionId === null) return;
    this.isLoading = true;

    forkJoin({
      collection: this.collectionService.getCollectionById(this.collectionId).pipe(
        catchError(err => {
          console.error('Error loading collection info', err);
          return of({ name: 'Collection' } as any);
        })
      ),
      documents: this.collectionService.getDocuments(this.collectionId).pipe(
        catchError(err => {
          console.error('Error loading documents', err);
          return of([]);
        })
      ),
      history: this.collectionService.getChatHistory(this.collectionId).pipe(
        catchError(err => {
          console.error('Error loading chat history', err);
          return of([]);
        })
      )
    }).pipe(
      finalize(() => this.isLoading = false)
    ).subscribe({
      next: (results) => {
        this.collectionName = results.collection.name || 'Collection';
        this.documents = results.documents.map(d => ({
          id: d.id,
          name: d.fileName || d.name,
          size: d.size || 'Unknown',
          uploadedAt: d.uploadedAt || 'Unknown',
          status: d.status || 'indexed'
        }));
        this.chatMessages = results.history.map(h => ({
          id: h.id,
          role: h.role,
          content: h.content,
          timestamp: h.timestamp || new Date().toLocaleTimeString()
        }));
        this.shouldScroll = true;
      },
      error: (err) => {
        console.error('Critical error loading collection data', err);
      }
    });
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

    this.collectionService.askQuestion(this.collectionId, { question }).subscribe({
      next: (response) => {
        const assistantMessage: ChatRecordModel = {
          id: response.id || ("msg-" + Date.now()),
          role: "assistant",
          content: response.answer || response.content,
          timestamp: new Date().toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
          }),
        };
        this.chatMessages.push(assistantMessage);
        this.shouldScroll = true;
      },
      error: (err) => {
        console.error('Error asking question', err);
        const errorMessage: ChatRecordModel = {
          id: "msg-" + Date.now(),
          role: "assistant",
          content: "Sorry, I encountered an error while processing your request.",
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
