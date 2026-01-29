import { Routes } from '@angular/router';
import {Landing} from './pages/landing/landing';
import {Login} from './pages/login/login';
import Register from './pages/register/register';
import {Collection} from './pages/collection/collection';
import {Collections} from './pages/collections/collections';
import {CollectionsLayout} from './pages/collections-layout/collections-layout';
import {CreateCollection} from './pages/create-collection/create-collection';
import {Settings} from './pages/settings/settings';
import {authGuardGuard} from './guards/auth.guard-guard';

export const routes: Routes = [
  { path: '', component: Landing },
  { path: 'login', component: Login },
  { path: 'register', component: Register},
  {
    path: 'collections',
    component: CollectionsLayout,
    canActivate: [authGuardGuard],
    children: [
      { path: '', component: Collections },
      { path: ':cid', component: Collection },
    ],
  },
  { path: 'create-collection', component: CreateCollection, canActivate: [authGuardGuard] },
  { path: 'settings', component: Settings, canActivate: [authGuardGuard] },
  // Redirect old URL to new nested URL
  { path: 'collection/:cid', redirectTo: 'collections/:cid', pathMatch: 'full' },
];
