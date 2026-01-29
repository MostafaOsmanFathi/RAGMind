import {Component, inject, OnDestroy, OnInit, PLATFORM_ID} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {CollectionModel} from '../../model/collection-model';
import {CollectionService} from '../../services/collection-service';
import {finalize, Subscription} from 'rxjs';
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
  collectionsData: CollectionModel[] = [];
  isLoading = true;
  private subscription?: Subscription;

  ngOnInit(): void {
    this.subscription = this.collectionService.collections$.subscribe({
      next: (data) => {
        this.collectionsData = data;
        // Even if empty, if we've received an emission after the initial one,
        // it means we have data (or lack thereof) to show.
        // However, we rely more on the refreshCollections call below for the initial load.
      }
    });

    if (isPlatformBrowser(this.platformId)) {
      this.collectionService.refreshCollections(20).pipe(
        finalize(() => this.isLoading = false)
      ).subscribe({
        error: (err) => console.error('Error refreshing collections', err)
      });
    } else {
      this.isLoading = false;
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  createCollection(): void {
    this.router.navigate(['/create-collection']);
  }
}
