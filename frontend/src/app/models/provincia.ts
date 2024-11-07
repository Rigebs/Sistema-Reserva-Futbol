import { Departamento } from "./departamento";

export interface Provincia {
  id?: number;
  nombre: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string;
  fechaModificacion?: Date;
  estado?: string;
  departamento: Departamento;
}
