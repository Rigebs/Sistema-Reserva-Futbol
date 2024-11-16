import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Persona } from "../models/persona";

@Injectable({
  providedIn: "root",
})
export class PersonaService {
  private apiUrl = `${environment.NG_APP_URL_API_GENERAL}/personas`;

  constructor(private http: HttpClient) {}

  // Método para consultar el DNI
  consultarDni(dni: string): Observable<any> {
    const url = `${this.apiUrl}/consultar-dni?dni=${dni}`;
    return this.http.post<any>(url, {});
  }

  // Obtener todas las personas
  obtenerPersonas(): Observable<Persona[]> {
    return this.http.get<Persona[]>(this.apiUrl);
  }

  // Obtener persona por ID
  obtenerPersonaPorId(id: number): Observable<Persona> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Persona>(url);
  }

  // Crear una nueva persona
  crearPersona(persona: Persona): Observable<any> {
    return this.http.post<any>(this.apiUrl, persona);
  }

  // Actualizar una persona
  actualizarPersona(id: number, persona: Persona): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<any>(url, persona);
  }

  // Cambiar el estado de la persona
  cambiarEstado(id: number, status: number): Observable<any> {
    const url = `${this.apiUrl}/${id}/status/${status}`;
    return this.http.patch<any>(url, {});
  }
}
