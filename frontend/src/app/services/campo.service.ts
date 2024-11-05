import { Injectable } from "@angular/core";
import { Campo } from "../models/campo";
import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { CampoSede } from "../models/campo-sede";

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

  getAllCampoSede(): Observable<CampoSede[]> {
    return this.http.get<CampoSede[]>(`${this.apiUrl}/with-sede`);
  }

  // Obtener un campo por ID
  getById(id: number): Observable<Campo> {
    return this.http.get<Campo>(`${this.apiUrl}/${id}`);
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
