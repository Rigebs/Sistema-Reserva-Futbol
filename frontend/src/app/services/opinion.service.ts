import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { Opinion } from "../models/opinion";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class OpinionService {
  private baseUrl = `${environment.NG_APP_URL_API_GENERAL}/opiniones`;

  constructor(private http: HttpClient) {}

  // Obtener todas las opiniones para una compañía específica
  obtenerOpinionesPorCompania(companiaId: number): Observable<Opinion[]> {
    const url = `${this.baseUrl}/compania/${companiaId}`;
    return this.http.get<Opinion[]>(url);
  }

  // Obtener todas las opiniones del usuario autenticado
  obtenerOpinionesPorUsuario(): Observable<Opinion[]> {
    const url = `${this.baseUrl}/user`;
    return this.http.get<Opinion[]>(url);
  }

  // Agregar una nueva opinión
  agregarOpinion(opinion: Opinion): Observable<Opinion> {
    console.log("LLEGO: ", opinion);

    return this.http.post<Opinion>(this.baseUrl, opinion);
  }

  // Actualizar una opinión existente (el autor debe ser el propietario)
  actualizarOpinion(id: number, opinion: Opinion): Observable<Opinion> {
    const url = `${this.baseUrl}/${id}`;
    return this.http.put<Opinion>(url, opinion);
  }
}
