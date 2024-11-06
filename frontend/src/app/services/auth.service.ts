import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Usuario } from "../models/usuario";
import { BehaviorSubject, tap } from "rxjs";
import { jwtDecode, JwtPayload } from "jwt-decode";
import { environment } from "../../environments/environment";

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private apiUrl = environment.NG_APP_URL_API_AUTH;
  private authTokenKey = "authToken";

  private currentUserSubject = new BehaviorSubject<string | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    console.log("HOLA", "API: ", this.apiUrl);

    this.loadUserFromToken();
  }

  login(user: Usuario) {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, user).pipe(
      tap((response) => {
        if (response && response.token) {
          this.storeToken(response.token);
        }
      })
    );
  }

  private storeToken(token: string) {
    localStorage.setItem(this.authTokenKey, token);
    this.setUserFromToken(token);
  }

  private setUserFromToken(token: string) {
    const decoded: JwtPayload = jwtDecode(token);
    this.currentUserSubject.next(decoded.sub || null);
  }

  private loadUserFromToken() {
    const token = localStorage.getItem(this.authTokenKey);
    if (token) {
      this.setUserFromToken(token);
    } else {
      this.currentUserSubject.next(null);
    }
  }

  logout() {
    localStorage.removeItem(this.authTokenKey);
    this.currentUserSubject.next(null);
  }
}
