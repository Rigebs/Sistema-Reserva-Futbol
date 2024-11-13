import { Campo } from "./campo";

export interface SedeWithCampo {
  userId: number;
  companiaId: number;
  companiaNombre: number;
  companiaImagenUrl: number;
  direccion: string;
  camposWithSede: Campo[];
}
