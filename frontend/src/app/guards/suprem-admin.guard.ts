import { Injectable } from "@angular/core";
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
} from "@angular/router";
import { Observable } from "rxjs";
import { AuthTokenUtil } from "../utils/auth-token-util";

@Injectable({
  providedIn: "root",
})
export class SupremAdminGuard implements CanActivate {
  constructor(private router: Router, private authTokenUtil: AuthTokenUtil) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    const usuarioEnRuta = next.url[0].path;
    const token = localStorage.getItem("authToken");

    if (token) {
      console.log("TOK");

      if (
        this.authTokenUtil.getUserFromToken() === usuarioEnRuta &&
        this.authTokenUtil.isAdmin()
      ) {
        return true;
      } else {
        this.router.navigate(["/home"]);
        return false;
      }
    } else {
      this.router.navigate(["/login"]);
      return false;
    }
  }
}
