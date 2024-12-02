import { Cliente } from "./cliente";
import { Compania } from "./compania";
import { Empresa } from "./empresa";

export interface UserDetails {
  username: string;
  email: string;
  roles?: string;
  compania?: Compania;
  empresa?: Empresa;
  cliente?: Cliente;
}
