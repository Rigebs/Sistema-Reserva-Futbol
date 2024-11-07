import { Empresa } from "./empresa";

export interface Compania {
  id?: number;
  nombre?: string;
  concepto?: string;
  correo?: string;
  pagWeb?: string;
  estado?: string;
  empresa?: Empresa;
}
