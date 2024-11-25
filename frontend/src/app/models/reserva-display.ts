export interface ReservaDisplay {
  reservaId: number; // ID de la reserva
  sede: string; // Nombre de la sede
  fechaReserva: string; // Fecha de la reserva (formato ISO)
  subtotal: number; // Subtotal de la reserva
  total: number; // Total de la reserva
  cliente: string;
}
