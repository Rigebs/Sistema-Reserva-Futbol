import { Provincia } from "./provincia";

export interface Distrito {
  id?: number;
  nombre?: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string;
  fechaModificacion?: Date;
  estado?: string;
  provincia?: Provincia;
}
