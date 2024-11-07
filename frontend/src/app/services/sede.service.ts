import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Sede } from "../models/sede";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class SedeService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/sedes`;

  constructor(private http: HttpClient) {}

  // Obtener todas las sedes
  getAllSedes(): Observable<Sede[]> {
    return this.http.get<Sede[]>(this.apiUrl);
  }

  // Obtener sede por ID
  getSedeById(id: number): Observable<Sede> {
    return this.http.get<Sede>(`${this.apiUrl}/${id}`);
  }

  // Crear nueva sede
  createSede(sede: Sede): Observable<any> {
    return this.http.post(this.apiUrl, sede);
  }

  // Actualizar sede
  updateSede(id: number, sede: Sede): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, sede);
  }

  // Cambiar estado de la sede
  changeStatus(id: number, status: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/status/${status}`, {});
  }

  // Obtener sedes por ID de la sucursal
  getSedesBySucursalId(sucursalId: number): Observable<Sede[]> {
    return this.http.get<Sede[]>(`${this.apiUrl}/sucursal/${sucursalId}`);
  }
}
