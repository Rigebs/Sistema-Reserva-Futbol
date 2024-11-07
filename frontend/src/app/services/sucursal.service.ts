import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Sucursal } from "../models/sucursal";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class SucursalService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/sucursales`;

  constructor(private http: HttpClient) {}

  // Obtener todas las sucursales
  getAllSucursales(): Observable<Sucursal[]> {
    return this.http.get<Sucursal[]>(this.apiUrl);
  }

  // Obtener sucursal por ID
  getSucursalById(id: number): Observable<Sucursal> {
    return this.http.get<Sucursal>(`${this.apiUrl}/${id}`);
  }

  // Crear nueva sucursal
  createSucursal(sucursal: Sucursal): Observable<any> {
    return this.http.post(this.apiUrl, sucursal);
  }

  // Actualizar sucursal
  updateSucursal(id: number, sucursal: Sucursal): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, sucursal);
  }

  // Cambiar estado de la sucursal
  changeStatus(id: number, status: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/status/${status}`, {});
  }

  // Obtener sucursales por ID de la compañía
  getSucursalesByCompaniaId(companiaId: number): Observable<Sucursal[]> {
    return this.http.get<Sucursal[]>(`${this.apiUrl}/compania/${companiaId}`);
  }
}
