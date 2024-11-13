import { Injectable } from "@angular/core";
import { Campo } from "../models/campo";
import { environment } from "../../environments/environment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
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

  // Guardar un nuevo campo
  save(campo: Campo): Observable<Campo> {
    return this.http.post<Campo>(this.apiUrl, campo);
  }

  // Actualizar un campo existente
  update(id: number, campo: Campo): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${id}`, campo);
  }

  // Cambiar el estado de un campo
  changeStatus(id: number, status: number): Observable<string> {
    return this.http.patch<string>(`${this.apiUrl}/${id}/status/${status}`, {});
  }
}
