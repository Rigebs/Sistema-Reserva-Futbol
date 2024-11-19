import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Time } from "@angular/common";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class AvailabilityCamposService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/availability`;

  constructor(private http: HttpClient) {}

  getAvailableHours(campoId: number, fecha: string): Observable<any> {
    const params = new HttpParams().set("fecha", fecha);
    return this.http.get<any>(`${this.apiUrl}/available-hours/${campoId}`, {
      params,
    });
  }
}
