export interface Opinion {
  id?: number;
  contenido: string;
  calificacion: number;
  usuarioCreacion?: string;
  fechaCreacion?: string;
  userId?: number;
  companiaId: number;
}
