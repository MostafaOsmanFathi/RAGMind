import {Component} from '@angular/core';
import {RouterLink} from '@angular/router';
import {CollectionModel} from '../../model/collection-model';

@Component({
  selector: 'app-collections',
  imports: [
    RouterLink
  ],
  templateUrl: './collections.html',
  styleUrl: './collections.scss',
})
export class Collections {
  collectionsData: CollectionModel[] = [
    {
      id: 1,
      name: "Q1 Reports",
      description: "Financial reports and analyses for Q1 2024",
      documentCount: 12,
    }
  ]

  createCollection(): void {

  }
}
