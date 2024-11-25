import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { ReservaDisplay } from "../models/reserva-display";

@Injectable({
  providedIn: "root",
})
export class VentaService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/reservas`;

  constructor(private http: HttpClient) {}

  getReservasForLoggedUser(): Observable<ReservaDisplay[]> {
    return this.http.get<ReservaDisplay[]>(`${this.apiUrl}/sede`);
  }
}
