import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { UpdateClientSede } from "../models/update-client-sede";
import { catchError, Observable, throwError } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class UsuarioService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/users`;

  constructor(private http: HttpClient) {}

  updateClientOrSede(updateRequest: UpdateClientSede): Observable<string> {
    return this.http
      .put(`${this.apiUrl}/updateClientOrSede`, updateRequest, {
        responseType: "text",
      })
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
}
