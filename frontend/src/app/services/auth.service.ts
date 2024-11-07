import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Usuario } from "../models/usuario";
import { BehaviorSubject, catchError, Observable, tap, throwError } from "rxjs";
import { jwtDecode, JwtPayload } from "jwt-decode";
import { environment } from "../../environments/environment";
import { UsuarioRegistro } from "../models/usuario-registro";
import { Verify } from "../models/verify-code";
import { PasswordReset } from "../models/password-reset";
import { PasswordResetRequest } from "../models/password-reset-request";
import { UpdateClientSede } from "../models/update-client-sede";

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private apiUrl = environment.NG_APP_URL_API_AUTH;
  private authTokenKey = "authToken";

  private currentUserSubject = new BehaviorSubject<string | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
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

  register(user: any): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/signup`, user, { responseType: "text" })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          if (
            error.status === 200 &&
            !error.ok &&
            typeof error.error === "string"
          ) {
            console.log("String de error recibido:", error.error);
            return throwError(() => new Error(error.error));
          } else {
            return throwError(() => error);
          }
        })
      );
  }

  verifyCode(verification: Verify): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/verify`, verification, { responseType: "text" })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          if (
            error.status === 200 &&
            !error.ok &&
            typeof error.error === "string"
          ) {
            console.log("String de error recibido:", error.error);
            return throwError(() => new Error(error.error));
          } else {
            return throwError(() => error);
          }
        })
      );
  }

  resendVerificationCode(email: string): Observable<any> {
    return this.http
      .post(
        `${this.apiUrl}/resend?email=${encodeURIComponent(email)}`,
        {},
        { responseType: "text" }
      )
      .pipe(
        catchError((error: HttpErrorResponse) => {
          return throwError(() => error);
        })
      );
  }

  // Solicitar restablecimiento de contraseña
  requestPasswordReset(
    passwordResetRequest: PasswordResetRequest
  ): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/password/reset-request`, passwordResetRequest, {
        responseType: "text",
      })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          return throwError(() => error);
        })
      );
  }

  // Restablecer contraseña
  resetPassword(passwordReset: PasswordReset): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/password/reset`, passwordReset, {
        responseType: "text",
      })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          return throwError(() => error);
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
