import {Injectable} from '@angular/core';
import {BehaviorSubject, throwError} from 'rxjs';
import {UserModel} from '../model/user-model'

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  //TODO Implement Auth Service
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private currentUserSubject = new BehaviorSubject<UserModel | null>(null);

  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  public currentUser$ = this.currentUserSubject.asObservable();

  isAuthenticated(): boolean {
    return false;
  }

  logout(): void {

  }


  register(name: string, email: string, password: string) {
    return throwError(() => new Error('Email already exists'));
  }

  login(email: string, password: string) {
    return throwError(() => new Error('Email already exists'));

  }
}
