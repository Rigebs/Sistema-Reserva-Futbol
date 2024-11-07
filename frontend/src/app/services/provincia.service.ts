import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { Provincia } from "../models/provincia";
import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class ProvinciaService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/provincia`;

  constructor(private http: HttpClient) {}

  // Obtener todas las provincias
  getAll(): Observable<Provincia[]> {
    return this.http.get<Provincia[]>(this.apiUrl);
  }

  // Obtener provincia por su id
  getById(id: number): Observable<Provincia> {
    return this.http.get<Provincia>(`${this.apiUrl}/${id}`);
  }

  // Obtener provincias por departamentoId
  getByDepartamentoId(departamentoId: number): Observable<Provincia[]> {
    return this.http.get<Provincia[]>(
      `${this.apiUrl}/departamento/${departamentoId}`
    );
  }
}
