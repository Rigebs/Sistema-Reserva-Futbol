import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthNotificationService } from './services/auth-notification.service';

@Injectable({
  providedIn: 'root'  // Esto asegura que Angular registre el guard como un servicio singleton
})

export class AuthGuard implements CanActivate {
  constructor(
    private router: Router,
    private authNotificationService: AuthNotificationService // Inyecta el servicio
  ) {}

  canActivate(): boolean {
    const token = localStorage.getItem('authToken');

    if (!token) {
      // Enviar mensaje al Navbar
      this.authNotificationService.sendMessage('Primero tienes que iniciar sesión');

      // Redirigir a la página de inicio
      this.router.navigate(['/home']);
      return false;
    }
    return true;
  }
}