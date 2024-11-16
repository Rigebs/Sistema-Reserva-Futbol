import { Injectable } from "@angular/core";
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
} from "@angular/router";
import { AuthService } from "../services/auth.service";
import { Observable } from "rxjs";
import { AuthTokenUtil } from "../utils/auth-token-util";

@Injectable({
  providedIn: "root",
})
export class AdminGuard implements CanActivate {
  constructor(private router: Router, private authTokenUtil: AuthTokenUtil) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    const usuarioEnRuta = next.url[0].path;
    const token = localStorage.getItem("authToken");

    if (token) {
      if (
        this.authTokenUtil.getUserFromToken() === usuarioEnRuta &&
        this.authTokenUtil.isCompania()
      ) {
        return true;
      } else {
        this.router.navigate(["/access-denied"]);
        return false;
      }
    } else {
      this.router.navigate(["/login"]);
      return false;
    }
  }
}
