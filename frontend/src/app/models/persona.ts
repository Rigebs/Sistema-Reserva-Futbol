import { Distrito } from "./distrito";

export interface Persona {
  id: number;
  dni: string;
  nombre: string;
  apePaterno: string;
  apeMaterno: string;
  celular: string;
  correo: string;
  fechaNac: string;
  genero: string;
  direccion: string;
  estado: string;
  distrito: Distrito;
}
