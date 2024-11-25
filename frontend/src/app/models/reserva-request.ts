import { DetalleVenta } from "./detalle-venta";
import { Reserva } from "./reserva";

export interface ReservaRequest {
  reservaDTO: Reserva;
  detallesVenta: DetalleVenta[];
}
