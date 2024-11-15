export interface Opinion {
  id?: number;
  contenido: string;
  calificacion: number;
  usuarioCreacion?: string;
  userId?: number;
  companiaId: number;
}
