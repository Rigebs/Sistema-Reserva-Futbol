import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { ReservaRequest } from "../models/reserva-request";
import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";
import { ReservaResponse } from "../models/reserva-response";

@Injectable({
  providedIn: "root",
})
export class ReservaService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/reservas`;

  constructor(private http: HttpClient) {}

  createReserva(request: ReservaRequest): Observable<ReservaResponse> {
    return this.http.post<ReservaResponse>(`${this.apiUrl}`, request);
  }
}
