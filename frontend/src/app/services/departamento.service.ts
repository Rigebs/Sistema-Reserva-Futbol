import { Injectable } from "@angular/core";
import { Departamento } from "../models/departamento";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class DepartamentoService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/departamento`;

  constructor(private http: HttpClient) {}

  // Obtener todos los departamentos
  getAll(): Observable<Departamento[]> {
    return this.http.get<Departamento[]>(this.apiUrl);
  }

  // Obtener un departamento por su id
  getById(id: number): Observable<Departamento> {
    return this.http.get<Departamento>(`${this.apiUrl}/${id}`);
  }
}
