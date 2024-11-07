import { Distrito } from "./distrito";

export interface Empresa {
  id?: number;
  ruc?: string;
  razonSocial?: string;
  telefono?: string;
  direccion?: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string;
  fechaModificacion?: Date;
  estado?: string;
  distrito?: Distrito;
}
