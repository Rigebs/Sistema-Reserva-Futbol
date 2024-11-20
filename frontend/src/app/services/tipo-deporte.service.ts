import { Injectable } from "@angular/core";
import { TipoDeporte } from "../models/tipo-deporte";
import { environment } from "../../environments/environment";
import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class TipoDeporteService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/tipo-deporte`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<TipoDeporte[]> {
    return this.http.get<TipoDeporte[]>(this.apiUrl);
  }

  getById(id: number): Observable<TipoDeporte> {
    return this.http.get<TipoDeporte>(`${this.apiUrl}/${id}`);
  }

  save(tipoDeporte: TipoDeporte): Observable<TipoDeporte> {
    return this.http.post<TipoDeporte>(this.apiUrl, tipoDeporte);
  }

  update(id: number, tipoDeporte: TipoDeporte): Observable<TipoDeporte> {
    return this.http.put<TipoDeporte>(`${this.apiUrl}/${id}`, tipoDeporte);
  }

  changeStatus(id: number, status: number): Observable<string> {
    return this.http.patch<string>(`${this.apiUrl}/${id}/status/${status}`, {});
  }
}
