import { Empresa } from "./empresa";
import { Imagen } from "./imagen";
import { QrImagen } from "./qr-imagen";

export interface Compania {
  id?: number;
  nombre?: string;
  concepto?: string;
  correo?: string;
  celular?: string;
  horaInicio?: string;
  horaFin?: string;
  empresa?: Empresa;
  imagen?: Imagen;
  qrImagen?: QrImagen;
}
