import { Routes } from '@angular/router';
import {Landing} from './pages/landing/landing';
import {Login} from './pages/login/login';
import {Register} from './pages/register/register';
import {Collection} from './pages/collection/collection';
import {Collections} from './pages/collections/collections';
import {Settings} from './pages/settings/settings';
import {authGuardGuard} from './guards/auth.guard-guard';

export const routes: Routes = [
  { path: '', component: Landing },
  { path: 'login', component: Login },
  { path: 'register', component: Register},
  { path: 'collections', component: Collections, canActivate: [authGuardGuard] },
  { path: 'collection/:cid', component: Collection, canActivate: [authGuardGuard] },
  { path: 'settings', component: Settings, canActivate: [authGuardGuard] },
];
