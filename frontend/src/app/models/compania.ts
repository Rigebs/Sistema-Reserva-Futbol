import { Empresa } from "./empresa";

export interface Compania {
  id?: number;
  nombre?: string;
  concepto?: string;
  correo?: string;
  celular?: string;
  horaInicio?: string;
  horaFin?: string;
  empresa?: Empresa;
}
