import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CollectionService } from '../../services/collection-service';

@Component({
  selector: 'app-create-collection',
  imports: [FormsModule, RouterLink],
  templateUrl: './create-collection.html',
  styleUrl: './create-collection.scss'
})
export class CreateCollection {
  collectionName: string = '';
  isLoading: boolean = false;
  error: string | null = null;

  private collectionService = inject(CollectionService);
  private router = inject(Router);

  onSubmit() {
    if (!this.collectionName.trim()) {
      this.error = 'Collection name is required';
      return;
    }

    this.isLoading = true;
    this.error = null;

    this.collectionService.addCollection(this.collectionName).subscribe({
      next: (collection) => {
        this.isLoading = false;
        this.router.navigate(['/collection', collection.id]);
      },
      error: (err) => {
        this.isLoading = false;
        this.error = 'Failed to create collection. Please try again.';
        console.error('Error creating collection', err);
      }
    });
  }
}
