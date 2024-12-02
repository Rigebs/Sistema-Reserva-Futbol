import { Injectable } from "@angular/core";
import { Campo } from "../models/campo";
import { environment } from "../../environments/environment";
import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
} from "@angular/common/http";
import { catchError, Observable, throwError } from "rxjs";
import { CampoSede } from "../models/campo-sede";
import { SedeWithCampo } from "../models/sede-with-campo";

@Injectable({
  providedIn: "root",
})
export class CampoService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/campos`;

  constructor(private http: HttpClient) {}

  // Obtener todos los campos
  getAll(): Observable<Campo[]> {
    return this.http.get<Campo[]>(this.apiUrl);
  }

  getAllCampoSede(
    distritoNombre: string = "",
    provinciaNombre: string = "",
    departamentoNombre: string = "",
    fechaReserva: string
  ): Observable<CampoSede[]> {
    let params = new HttpParams()
      .set("distritoNombre", distritoNombre)
      .set("provinciaNombre", provinciaNombre)
      .set("departamentoNombre", departamentoNombre)
      .set("fechaReserva", fechaReserva);

    return this.http.get<CampoSede[]>(`${this.apiUrl}/available-sedes`, {
      params,
    });
  }

  // Obtener un campo por ID
  getById(id: number): Observable<Campo> {
    return this.http.get<Campo>(`${this.apiUrl}/${id}`);
  }

  getCamposByUsuarioId(usuarioId: number): Observable<SedeWithCampo> {
    return this.http.get<SedeWithCampo>(
      `${this.apiUrl}/usuario/${usuarioId}/with-sede`
    );
  }

  getCamposByCompania(): Observable<Campo[]> {
    return this.http.get<Campo[]>(`${this.apiUrl}/compania`);
  }

  // Guardar un nuevo campo
  save(campo: Campo): Observable<any> {
    return this.http
      .post(this.apiUrl, campo, { responseType: "text" }) // Espera un texto como respuesta
      .pipe(
        catchError((error: HttpErrorResponse) => {
          // Si hay un error y el backend responde con un texto
          if (
            error.status === 200 &&
            !error.ok &&
            typeof error.error === "string"
          ) {
            console.log("String de error recibido:", error.error);
            return throwError(() => new Error(error.error)); // Lanza el error recibido
          } else {
            return throwError(() => error); // Lanza el error original
          }
        })
      );
  }
  update(id: number, campo: Campo): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${id}`, campo, {
      responseType: "text" as "json",
    });
  }

  changeStatus(id: number, status: string): Observable<any> {
    console.log("st: ", status);

    return this.http
      .patch(
        `${this.apiUrl}/${id}/status/${status}`,
        {},
        { responseType: "text" }
      )
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
