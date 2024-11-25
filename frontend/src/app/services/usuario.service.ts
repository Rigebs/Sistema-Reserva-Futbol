import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { BehaviorSubject, catchError, Observable, tap, throwError } from "rxjs";
import { UpdateClientCompania } from "../models/update-client-sede";
import { jwtDecode, JwtPayload } from "jwt-decode";

@Injectable({
  providedIn: "root",
})
export class UsuarioService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/users`;

  private authTokenKey = "authToken";

  private currentUserSubject = new BehaviorSubject<string | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  updateClientOrSede(updateRequest: UpdateClientCompania) {
    return this.http
      .put(`${this.apiUrl}/updateClientOrSede`, updateRequest, {
        responseType: "json",
      })
      .pipe(
        tap((response: any) => {
          if (response && response.token) {
            this.storeToken(response.token);
          } else {
            console.error("Token no encontrado en la respuesta:", response);
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

  updateRoleCompania(idCompania: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/compania/${idCompania}/updateRoleCompania`,
      {}
    );
  }

  // Método para rechazar la compañía
  rejectCompania(idCompania: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/compania/${idCompania}/reject`);
  }
}
