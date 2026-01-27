import {Component, ElementRef, ViewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {DocumentModel} from '../../model/document-model';
import {ChatRecordModel} from '../../model/chat-record-model';

@Component({
  selector: 'app-collection',
  imports: [
    FormsModule
  ],
  templateUrl: './collection.html',
  styleUrl: './collection.scss',
})
export class Collection {
  collectionName = "Collection";
  messageInput = "";
  private shouldScroll = true;

  @ViewChild("messagesContainer") messagesContainer!: ElementRef<HTMLDivElement>;
  documents: DocumentModel[] = [
    {
      id: "doc-1",
      name: "Annual_Report_2024.pdf",
      size: "2.4 MB",
      uploadedAt: "Today at 10:30 AM",
      status: "indexed",
    }];
  chatMessages: ChatRecordModel[] = []

  constructor(private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const collectionId = params.get("cid");
      this.collectionName = `Collection ${collectionId}`;
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
    if (!this.messageInput.trim()) return;

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
    this.messageInput = "";
    this.shouldScroll = true;

    //TODO remove it and will be handled with websocket connection later
    // Simulate assistant response
    setTimeout(() => {
      const assistantMessage: ChatRecordModel = {
        id: "msg-" + (Date.now() + 1),
        role: "assistant",
        content: "This is a mock response. Real responses will come from the backend API.",
        timestamp: new Date().toLocaleTimeString([], {
          hour: "2-digit",
          minute: "2-digit",
        }),
      };
      this.chatMessages.push(assistantMessage);
      this.shouldScroll = true;
    }, 500);
  }

  protected onUploadClick() {

  }
}
