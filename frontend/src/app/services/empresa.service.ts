import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Empresa } from "../models/empresa";

@Injectable({
  providedIn: "root",
})
export class EmpresaService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/empresa`;

  constructor(private http: HttpClient) {}

  // Consultar RUC
  consultarRuc(ruc: string): Observable<any> {
    // Hacemos una solicitud POST, pero pasamos el parámetro 'ruc' en la URL
    return this.http.post(`${this.apiUrl}/consultar-ruc?ruc=${ruc}`, {});
  }

  // Obtener todas las empresas
  getAllEmpresas(): Observable<Empresa[]> {
    return this.http.get<Empresa[]>(this.apiUrl);
  }

  // Obtener empresa por ID
  getEmpresaById(id: number): Observable<Empresa> {
    return this.http.get<Empresa>(`${this.apiUrl}/${id}`);
  }

  // Crear nueva empresa
  createEmpresa(empresa: Empresa): Observable<any> {
    return this.http.post(this.apiUrl, empresa);
  }

  // Actualizar empresa
  updateEmpresa(id: number, empresa: Empresa): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, empresa);
  }

  // Cambiar estado de la empresa
  changeStatus(id: number, status: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/status/${status}`, {});
  }
}
