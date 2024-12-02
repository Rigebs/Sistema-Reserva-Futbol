import { Component } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatTableModule } from "@angular/material/table";
import { Router } from "@angular/router";
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { CommonModule, DatePipe } from "@angular/common";
import { ReservaService } from "../../services/reserva.service";
import { Reserva } from "../../models/reserva";
import { DetalleVenta } from "../../models/detalle-venta";
import { ReservaRequest } from "../../models/reserva-request";
import { ReservaResponse } from "../../models/reserva-response";
import { MatSnackBar } from "@angular/material/snack-bar";
import { CompaniaService } from "../../services/compania.service";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { PagoDialogComponent } from "../../components/pago-dialog/pago-dialog.component";
import { UsuarioService } from "../../services/usuario.service";
import { AuthTokenUtil } from "../../utils/auth-token-util";
import { MatSelectModule } from "@angular/material/select";

interface RangoHora {
  horaInicio: number;
  horaFin: number;
}

interface ObjetoConRangos {
  campoId: number;
  fecha: string;
  rangosHora: RangoHora[];
}

@Component({
  selector: "app-pasarela-pago",
  standalone: true,
  imports: [
    MatCardModule,
    MatTableModule,
    CommonModule,
    MatButtonModule,
    NavbarComponent,
    MatSelectModule,
  ],
  templateUrl: "./pasarela-pago.component.html",
  styleUrl: "./pasarela-pago.component.css",
  providers: [DatePipe, MatDialogModule],
})
export class PasarelaPagoComponent {
  reservas: any[] = [];
  total: number = 0;
  subtotal: number = 0;

  reservaRequest: ReservaRequest | undefined;

  qrImageUrl: string | undefined;

  opciones: string[] = [];

  opcionSeleccionada: string = "";

  avalaibleButton: boolean = false;

  id: number | undefined;

  descuento = 0;
  igv = 0;

  disabled: boolean = true;

  dataEnviar: any;

  constructor(
    private router: Router,
    private datePipe: DatePipe,
    private reservaService: ReservaService,
    private snackBar: MatSnackBar,
    private companiaService: CompaniaService,
    private dialog: MatDialog,
    usuarioService: UsuarioService,
    authTokenUtil: AuthTokenUtil
  ) {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state as { reservas?: any[] };
    const stateQR = navigation?.extras?.state as { companiaId?: any };

    console.log("RESERVAS: ", state?.reservas);
    console.log("QR: ", stateQR?.companiaId);

    usuarioService.getUserById(authTokenUtil.getIdFromToken()).subscribe({
      next: (data) => {
        this.disabled = false;
        if (data.empresa) {
          this.opciones = ["FACTURA", "TICKET"];
          console.log("HKDF");
        } else {
          console.log("PERSO");
          this.opciones = ["BOLETA", "TICKET"];
        }
      },
    });

    if (stateQR?.companiaId) {
      // Realizamos la solicitud al servicio para obtener la información de pago
      this.companiaService.getPagoInfo(stateQR?.companiaId).subscribe({
        next: (data) => {
          this.qrImageUrl = data.qrImagenUrl; // Usar qrImagenUrl (con "i" mayúscula, como en la respuesta)
        },
        error: (err) => {
          console.error("Error al obtener el QR: ", err);
        },
      });
    } else {
      console.error("CompaniaId no disponible en el estado.");
    }

    console.log("qr: ", this.qrImageUrl);

    if (state?.reservas) {
      // Convertir horas y asignar reservas
      this.reservas = state.reservas.map((reserva) => ({
        ...reserva,
        horaInicio: this.convertirAHora(reserva.horaInicio),
        horaFinal: this.convertirAHora(reserva.horaFinal),
      }));

      this.subtotal = this.reservas.reduce(
        (acc, reserva) => acc + reserva.precio,
        0
      );

      // Calcular totales
      this.calcularTotal();
      console.log("hola");

      const subtotal = this.reservas.reduce(
        (acc, detalle) => acc + detalle.precio,
        0
      );
      const totalDescuento = this.subtotal - this.descuento;

      // Crear el objeto reserva
      const reserva: Reserva = {
        fecha: this.fechaConverter(new Date()),
        descuento: 0, // Redondear a 2 decimales
        total: parseFloat(this.total.toFixed(2)),
        igv: parseFloat(this.igv.toFixed(2)),
        subtotal: parseFloat(this.subtotal.toFixed(2)),
        totalDescuento: parseFloat(totalDescuento.toFixed(2)),
        tipoComprobante: this.obtenerTipoComprobante(this.opcionSeleccionada),
        metodoPagoId: 1,
      };

      console.log("RESERr: ", reserva);

      // Los detalles se pasan directamente
      const detalles: DetalleVenta[] = this.reservas.map((reserva) => ({
        ...reserva,
        horaInicio: this.convertirNormal(reserva.horaInicio),
        horaFinal: this.convertirNormal(reserva.horaFinal),
        fecha: this.fechaConverter(reserva.fecha),
      }));

      this.reservaRequest = {
        reservaDTO: reserva,
        detallesVenta: detalles,
      };
      console.log("REEE: ", this.reservaRequest);
    } else {
      // Redirigir si no hay reservas
      this.router.navigate(["/reservar-campo"]);
    }
  }

  obtenerTipoComprobante(opcion: string) {
    switch (opcion) {
      case "TICKET":
        return "T";
      case "BOLETA":
        return "B";
      case "FACTURA":
        return "F";
      default:
        return "";
    }
  }

  onSelectionChange(event: any) {
    this.opcionSeleccionada = event.value;
    console.log(this.opcionSeleccionada);
  }

  convertirNormal(hora: Date): string {
    if (!hora) {
      console.error("La hora proporcionada es inválida");
      return "";
    }

    // Convertir el objeto Date a una cadena con solo la hora (HH:mm:ss)
    const horas = hora.getHours().toString().padStart(2, "0");
    const minutos = hora.getMinutes().toString().padStart(2, "0");
    const segundos = hora.getSeconds().toString().padStart(2, "0");

    const horaConvertida = `${horas}:${minutos}:${segundos}`;

    console.log("Hora convertida:", horaConvertida);
    return horaConvertida;
  }

  openDialog(): void {
    const qrImageUrl = this.qrImageUrl;
    console.log("DFJK: ", this.id);

    if (qrImageUrl) {
      this.dialog.open(PagoDialogComponent, {
        width: "300px",
        data: {
          qrUrl: qrImageUrl,
          id: this.id,
          data: this.dataEnviar,
        },
      });
    } else {
      console.error("No se ha recibido el QR Image URL.");
    }
  }

  fechaConverter(fecha: Date): string {
    if (!fecha) {
      console.error("La fecha proporcionada es inválida");
      return "";
    }

    // Obtener los componentes de la fecha
    const anio = fecha.getFullYear();
    const mes = (fecha.getMonth() + 1).toString().padStart(2, "0"); // Los meses en JS comienzan en 0 (enero = 0)
    const dia = fecha.getDate().toString().padStart(2, "0");

    const fechaConvertida = `${anio}-${mes}-${dia}`;

    console.log("Fecha convertida:", fechaConvertida);
    return fechaConvertida;
  }

  convertirAHora(hora: any): Date {
    console.log("GO: ", hora);

    if (typeof hora !== "string") {
      console.error(`Hora inválida: ${hora}`);
      return new Date(); // O retornar null si prefieres
    }

    const [hours, minutes] = hora.split(":");
    const fecha = new Date();
    fecha.setHours(parseInt(hours, 10), parseInt(minutes, 10), 0);
    return fecha;
  }

  calcularTotal() {
    const porcentajeDescuento = 0; // 10% de descuento
    const porcentajeIgv = 0.18; // 18% de IGV

    this.descuento = this.subtotal * porcentajeDescuento;

    this.igv = this.subtotal * porcentajeIgv;

    this.total = this.subtotal - this.descuento + this.igv;
  }

  pagarConYape() {
    console.log(this.obtenerTipoComprobante(this.opcionSeleccionada));
    console.log(this.reservaRequest);

    // Verifica si reservaRequest no es undefined antes de acceder a sus propiedades
    if (this.reservaRequest) {
      this.reservaRequest.reservaDTO.tipoComprobante =
        this.obtenerTipoComprobante(this.opcionSeleccionada);

      this.reservaService.createReserva(this.reservaRequest).subscribe(
        (data: ReservaResponse) => {
          this.id = data.reservaId;

          this.avalaibleButton = true;
          this.snackBar.open("RESERVA REALIZADA CON ÉXITO", "Cerrar", {
            duration: 3000,
            horizontalPosition: "center",
            verticalPosition: "bottom",
          });
          console.log("D: ", data);
          this.dataEnviar = data;
        },
        (error) => {
          this.snackBar.open("Error al realizar la reserva", "Cerrar", {
            duration: 3000,
            horizontalPosition: "center",
            verticalPosition: "bottom",
          });

          console.error("ERROR: ", error);
        }
      );
    } else {
      console.error("Error: reservaRequest no está definido.");
    }
  }

  formatDate(date: Date): string {
    return this.datePipe.transform(date, "dd/MM/yyyy") || "";
  }
}
