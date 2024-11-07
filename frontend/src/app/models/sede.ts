import { Sucursal } from "./sucursal";

export interface Sede {
  id?: number;
  nombre: string;
  horaInicio: string;
  horaFin: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string;
  fechaModificacion?: Date;
  estado?: string;
  sucursal: Sucursal;
}
