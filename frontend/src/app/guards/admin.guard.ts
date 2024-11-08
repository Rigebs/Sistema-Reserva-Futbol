import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    const usuarioEnRuta = next.url[0].path;
    const token = localStorage.getItem('authToken');
    
    if (token) {
      const decodedToken: any = this.authService.decodeToken(token);
      const usuarioToken = decodedToken.sub;
      console.log("USUARIO: ", usuarioToken);
      

      if (usuarioToken === usuarioEnRuta) {
        return true;
      } else {
        this.router.navigate(['/access-denied']);
        return false;
      }
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}