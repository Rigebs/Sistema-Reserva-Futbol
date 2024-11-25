import { DetalleVenta } from "./detalle-venta";

export interface ReservaResponse {
  reservaId: number;
  cliente: string;
  direccionCliente: string;
  identificacion: string;
  celular: string;
  comprobante: string;
  igv: number;
  descuento: number;
  fecha: string;
  fechaCreacion: string;
  subtotal: number;
  total: number;
  numero: string;
  estado: string;
  cambio: number;
  serie: string;
  razonSocial: string;
  ruc: string;
  telefonoEmpresa: string;
  direccionEmpresa: string;
  concepto: string;
  imageUrl: string;
  detallesVenta: DetalleVenta[];
}
