import { Compania } from "./compania";

export interface Sucursal {
  id?: number;
  nombre?: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string;
  fechaModificacion?: Date;
  estado?: string;
  compania?: Compania;
}
