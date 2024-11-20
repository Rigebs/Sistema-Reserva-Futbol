import { TipoDeporte } from "./tipo-deporte";

export interface Campo {
  id: number;
  nombre: string;
  precio: number;
  descripcion: string;
  estado: string;
  usuarioId: number;
  tipoDeporte: TipoDeporte;
}
