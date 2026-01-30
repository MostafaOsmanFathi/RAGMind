import {ChangeDetectorRef, Component, inject, OnDestroy, OnInit, PLATFORM_ID} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {CollectionModel} from '../../model/collection-model';
import {CollectionService} from '../../services/collection-service';
import {finalize, Subscription, timeout} from 'rxjs';
import {isPlatformBrowser} from '@angular/common';

@Component({
  selector: 'app-collections',
  imports: [
    RouterLink
  ],
  templateUrl: './collections.html',
  styleUrl: './collections.scss',
})
export class Collections implements OnInit, OnDestroy {
  private collectionService = inject(CollectionService);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);
  collectionsData: CollectionModel[] = [];
  isLoading = true;
  loadError: string | null = null;
  private subscription?: Subscription;

  ngOnInit(): void {
    this.subscription = this.collectionService.collections$.subscribe({
      next: (data) => {
        this.collectionsData = data;
        this.cdr.markForCheck();
      }
    });

    if (isPlatformBrowser(this.platformId)) {
      this.loadCollections();
    } else {
      this.isLoading = false;
    }
  }

  loadCollections(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.loadError = null;
    this.isLoading = true;
    this.collectionService.refreshCollections().pipe(
      timeout(15000),
      finalize(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.loadError = null;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error refreshing collections', err);
        this.loadError = err?.name === 'TimeoutError'
          ? 'Request timed out. Check that the API is running at the configured URL.'
          : 'Failed to load collections. Check your connection and that the API is reachable.';
        this.cdr.markForCheck();
      }
    });
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  createCollection(): void {
    this.router.navigate(['/create-collection']);
  }
}
