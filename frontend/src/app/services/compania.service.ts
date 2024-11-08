import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { Compania } from "../models/compania";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class CompaniaService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/companias`;

  constructor(private http: HttpClient) {}

  // Guardar una nueva compañía
  saveCompania(
    qrFile: File,
    file: File,
    compania: Compania
  ): Observable<Compania> {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("qrFile", qrFile);
    formData.append("compania", JSON.stringify(compania));
    console.log("FILE: ", file);
    console.log("FILE qr: ", qrFile);

    console.log("FORM: ", formData);

    return this.http.post<Compania>(this.apiUrl, formData);
  }

  // Obtener todas las compañías
  getAllCompanias(): Observable<Compania[]> {
    return this.http.get<Compania[]>(this.apiUrl);
  }

  // Obtener compañía por ID
  getCompaniaById(id: number): Observable<Compania> {
    return this.http.get<Compania>(`${this.apiUrl}/${id}`);
  }

  // Cambiar estado de la compañía
  changeStatus(id: number, status: number): Observable<Compania> {
    return this.http.patch<Compania>(
      `${this.apiUrl}/status/${id}?status=${status}`,
      {}
    );
  }

  // Actualizar la imagen de la compañía
  updateCompaniaImage(id: number, file: File): Observable<Compania> {
    const formData = new FormData();
    formData.append("file", file);

    return this.http.put<Compania>(`${this.apiUrl}/${id}/imagen`, formData);
  }
}
