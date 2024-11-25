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
  fecha: string; // Usamos string para manejar fechas en formato ISO
  fechaCreacion: string; // LocalDateTime en Java -> string en formato ISO
  subtotal: number;
  total: number;
  numero: string;
  estado: string; // char en Java -> string (un solo carácter) en TypeScript
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
