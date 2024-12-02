import { Empresa } from "./empresa";
import { Persona } from "./persona";

export interface Cliente {
  id?: number;
  persona?: Persona;
  empresa?: Empresa;
}
