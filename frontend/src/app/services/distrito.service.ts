import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { Distrito } from "../models/distrito";
import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class DistritoService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/distrito`;

  constructor(private http: HttpClient) {}

  // Obtener todos los distritos
  getAll(): Observable<Distrito[]> {
    return this.http.get<Distrito[]>(this.apiUrl);
  }

  // Obtener un distrito por su id
  getById(id: number): Observable<Distrito> {
    return this.http.get<Distrito>(`${this.apiUrl}/${id}`);
  }

  // Obtener distritos por provinciaId
  getByProvinciaId(provinciaId: number): Observable<Distrito[]> {
    return this.http.get<Distrito[]>(`${this.apiUrl}/provincia/${provinciaId}`);
  }
}
