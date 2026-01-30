import { Injectable, signal, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

const STORAGE_KEY = 'ragmind-theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly platformId = inject(PLATFORM_ID);
  readonly isLightMode = signal(false);

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      const saved = localStorage.getItem(STORAGE_KEY);
      const preferLight = saved === 'light' || (saved === null && this.prefersLightMedia());
      this.isLightMode.set(preferLight);
      this.applyToDocument(preferLight);
    }
  }

  private prefersLightMedia(): boolean {
    if (typeof window === 'undefined' || !window.matchMedia) return false;
    return window.matchMedia('(prefers-color-scheme: light)').matches;
  }

  toggleTheme(): void {
    const next = !this.isLightMode();
    this.isLightMode.set(next);
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(STORAGE_KEY, next ? 'light' : 'dark');
      this.applyToDocument(next);
    }
  }

  private applyToDocument(isLight: boolean): void {
    if (typeof document === 'undefined') return;
    const html = document.documentElement;
    if (isLight) {
      html.classList.add('light');
    } else {
      html.classList.remove('light');
    }
  }
}
