import {CanActivateFn, Router} from '@angular/router';
import {inject, PLATFORM_ID} from '@angular/core';
import {AuthService} from '../services/auth-service';
import {map, take, switchMap, of} from 'rxjs';
import {isPlatformServer} from '@angular/common';

export const authGuardGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  if (isPlatformServer(platformId)) {
    return true;
  }

  return authService.isAuthenticated$.pipe(
    take(1),
    switchMap(isAuthenticated => {
      if (isAuthenticated) return of(true);
      return authService.tryRefreshFromStoredToken().pipe(
        take(1),
        map(success => {
          if (success) return true;
          router.navigate(['/login']);
          return false;
        })
      );
    })
  );
};
