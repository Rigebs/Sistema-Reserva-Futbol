import { Campo } from "./campo";

export interface SedeWithCampo {
  userId: number;
  companiaId: number;
  companiaNombre: string;
  companiaImagenUrl: number;
  horaInicio?: string;
  horaFin?: string;
  direccion: string;
  camposWithSede: Campo[];
}
