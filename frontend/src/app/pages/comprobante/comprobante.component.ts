import { Component } from "@angular/core";

@Component({
  selector: "app-comprobante",
  standalone: true,
  imports: [],
  templateUrl: "./comprobante.component.html",
  styleUrl: "./comprobante.component.css",
})
export class ComprobanteComponent {
  data = {
    cliente: "RICARDO ANDRES OBREGON LARA",
    direccionCliente: "Manuel Pardo N° 1017",
    identificacion: "73047218",
    celular: "903019613",
    comprobante: "FACTURA",
    igv: 6.48,
    descuento: 4,
    fecha: "2024-12-01",
    subtotal: 40,
    total: 42.48,
    numero: "000004",
    serie: "B001",
    razonSocial: "CLINICA SAN GABRIEL S.A.C.",
    ruc: "20505018509",
    telefonoEmpresa: "912232434",
    direccionEmpresa: "AV. LA MARINA NRO 2955 URB. MARANGA III ETAPA 4",
    detallesVenta: [
      {
        campoNombre: "FUTBOL 5",
        precio: 40,
      },
    ],
  };
}
