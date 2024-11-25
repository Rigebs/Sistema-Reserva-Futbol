export interface Reserva {
  fecha: string;
  descuento: number;
  total: number;
  igv: number;
  subtotal: number;
  totalDescuento: number;
  tipoComprobante: string;
  metodoPagoId: number;
}
