import {Injectable, inject, PLATFORM_ID} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {CollectionModel} from '../model/collection-model';
import {AuthService} from './auth-service';
import {API_BASE_URL} from '../config/api-config';
import {isPlatformBrowser} from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class CollectionService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private baseUrl = inject(API_BASE_URL);
  private platformId = inject(PLATFORM_ID);
  private apiUrl = `${this.baseUrl}/rag/collection`;
  private collectionsSubject = new BehaviorSubject<CollectionModel[]>([]);
  public collections$ = this.collectionsSubject.asObservable();

  constructor() {
  }

  private getHeaders(): HttpHeaders {
    const user = this.authService.currentUserValue;
    const token = user?.accessToken;
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getAllCollections(limit: number = 5, page: number = 0): Observable<CollectionModel[]> {
    if (!isPlatformBrowser(this.platformId)) {
      return of([]);
    }

    const user = this.authService.currentUserValue;
    if (!user || !user.accessToken) {
      return of([]);
    }

    return this.http.get<any[]>(this.apiUrl, {
      headers: this.getHeaders(),
      params: { limit: limit.toString(), page: page.toString() }
    }).pipe(
      map(collections => collections.map(c => this.mapToCollectionModel(c)))
    );
  }

  refreshCollections(limit: number = 5, page: number = 0): Observable<CollectionModel[]> {
    return this.getAllCollections(limit, page).pipe(
      tap(collections => this.collectionsSubject.next(collections))
    );
  }

  getCollectionById(id: number): Observable<CollectionModel> {
    if (!isPlatformBrowser(this.platformId)) {
      return of({} as CollectionModel);
    }
    const user = this.authService.currentUserValue;
    if (!user || !user.accessToken) {
      return of({} as CollectionModel);
    }
    return this.http.get<any>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    }).pipe(
      map(c => this.mapToCollectionModel(c))
    );
  }

  addCollection(name: string): Observable<CollectionModel> {
    return this.http.post<any>(this.apiUrl, { collectionName: name }, {
      headers: this.getHeaders()
    }).pipe(
      map(c => this.mapToCollectionModel(c)),
      tap(() => {
        this.authService.refreshUserData().subscribe();
        this.refreshCollections().subscribe();
      })
    );
  }

  deleteCollection(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    }).pipe(
      tap(() => {
        this.authService.refreshUserData().subscribe();
        this.refreshCollections().subscribe();
      })
    );
  }

  private mapToCollectionModel(c: any): CollectionModel {
    return {
      id: c.id,
      name: c.collectionName,
      documentCount: c.numberOfDocs,
      description: ''
    };
  }

  // Document management
  getDocuments(collectionId: number): Observable<any[]> {
    if (!isPlatformBrowser(this.platformId)) {
      return of([]);
    }
    const user = this.authService.currentUserValue;
    if (!user || !user.accessToken) {
      return of([]);
    }
    return this.http.get<any[]>(`${this.apiUrl}/${collectionId}/documents`, {
      headers: this.getHeaders()
    });
  }

  getDocumentById(collectionId: number, documentId: number): Observable<any> {
    if (!isPlatformBrowser(this.platformId)) {
      return of({});
    }
    const user = this.authService.currentUserValue;
    if (!user || !user.accessToken) {
      return of({});
    }
    return this.http.get<any>(`${this.apiUrl}/${collectionId}/documents/${documentId}`, {
      headers: this.getHeaders()
    });
  }

  addDocument(collectionId: number, file: File): Observable<any> {
    if (!isPlatformBrowser(this.platformId)) {
      return of({});
    }
    const user = this.authService.currentUserValue;
    if (!user || !user.accessToken) {
      return of({});
    }
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/${collectionId}/documents`, formData, {
      headers: this.getHeaders()
    }).pipe(
      tap(() => {
        this.authService.refreshUserData().subscribe();
        this.refreshCollections().subscribe();
      })
    );
  }

  // Queries / Chat (RagQueryController)
  // Note: Backend uses plural 'collections' for queries
  private queryApiUrl(collectionId: string | number) {
    return `${this.baseUrl}/rag/collections/${collectionId}/queries`;
  }

  askQuestion(collectionId: string | number, questionData: any): Observable<any> {
    if (!isPlatformBrowser(this.platformId)) {
      return of({});
    }
    const user = this.authService.currentUserValue;
    if (!user || !user.accessToken) {
      return of({});
    }
    return this.http.post(`${this.queryApiUrl(collectionId)}/ask`, questionData, {
      headers: this.getHeaders()
    });
  }

  getChatHistory(collectionId: string | number): Observable<any[]> {
    if (!isPlatformBrowser(this.platformId)) {
      return of([]);
    }
    const user = this.authService.currentUserValue;
    if (!user || !user.accessToken) {
      return of([]);
    }
    return this.http.get<any[]>(`${this.queryApiUrl(collectionId)}/chat-history`, {
      headers: this.getHeaders()
    });
  }
}
