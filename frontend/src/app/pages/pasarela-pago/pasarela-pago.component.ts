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

@Component({
  selector: "app-pasarela-pago",
  standalone: true,
  imports: [
    MatCardModule,
    MatTableModule,
    CommonModule,
    MatButtonModule,
    NavbarComponent,
  ],
  templateUrl: "./pasarela-pago.component.html",
  styleUrl: "./pasarela-pago.component.css",
  providers: [DatePipe, MatDialogModule],
})
export class PasarelaPagoComponent {
  reservas: any[] = [];
  total: number = 0;

  reservaRequest: ReservaRequest | undefined;

  qrImageUrl: string | undefined;

  constructor(
    private router: Router,
    private datePipe: DatePipe,
    private reservaService: ReservaService,
    private snackBar: MatSnackBar,
    private companiaService: CompaniaService,
    private dialog: MatDialog
  ) {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state as { reservas?: any[] };
    const stateQR = navigation?.extras?.state as { companiaId?: any };

    console.log("RESERVAS: ", state?.reservas);
    console.log("QR: ", stateQR?.companiaId);

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

      // Calcular totales
      this.calcularTotal();

      // Crear objeto reserva con cálculos dinámicos
      const subtotal = this.reservas.reduce(
        (acc, detalle) => acc + detalle.precio,
        0
      ); // Sumatoria de los precios
      const descuento = 0.1 * subtotal; // Supongamos un 10% de descuento (ajustar según lógica)
      const totalDescuento = subtotal - descuento; // Total menos descuento
      const igv = 0.18 * totalDescuento; // 18% de IGV (ajustar según lógica fiscal)
      const total = totalDescuento + igv; // Total final

      // Crear el objeto reserva
      const reserva: Reserva = {
        fecha: this.fechaConverter(new Date()), // Fecha en formato ISO
        descuento: parseFloat(descuento.toFixed(2)), // Redondear a 2 decimales
        total: parseFloat(total.toFixed(2)), // Redondear a 2 decimales
        igv: parseFloat(igv.toFixed(2)), // Redondear a 2 decimales
        subtotal: parseFloat(subtotal.toFixed(2)), // Redondear a 2 decimales
        totalDescuento: parseFloat(totalDescuento.toFixed(2)), // Redondear a 2 decimales
        tipoComprobante: "B", // Factura boleta, ajustar si necesario
        metodoPagoId: 1, // Método de pago por defecto
      };

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
    } else {
      // Redirigir si no hay reservas
      this.router.navigate(["/reservar-campo"]);
    }
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
    // Aquí pasamos la URL de la imagen QR
    const qrImageUrl = this.qrImageUrl; // Usamos la variable que contiene el URL recibido

    if (qrImageUrl) {
      this.dialog.open(PagoDialogComponent, {
        width: "300px",
        data: {
          qrUrl: qrImageUrl, // Pasamos la URL al diálogo
        },
      });
      this.pagarConYape();
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
    this.total = this.reservas.reduce(
      (acc, reserva) => acc + reserva.precio,
      0
    );
  }

  pagarConYape() {
    this.reservaService.createReserva(this.reservaRequest!).subscribe(
      (data: ReservaResponse) => {
        // Mostrar el Snackbar de éxito
        this.snackBar.open("RESERVA REALIZADA CON ÉXITO", "Cerrar", {
          duration: 3000, // Duración en milisegundos
          horizontalPosition: "center", // Posición horizontal
          verticalPosition: "bottom", // Posición vertical
        });

        console.log("RESPONSE: ", data); // Si necesitas más información
      },
      (error) => {
        // Si hay un error, podrías mostrar un Snackbar de error
        this.snackBar.open("Error al realizar la reserva", "Cerrar", {
          duration: 3000,
          horizontalPosition: "center",
          verticalPosition: "bottom",
        });

        console.error("ERROR: ", error); // Mostrar el error en consola
      }
    );

    console.log("REQUEST: ", this.reservaRequest);
  }

  formatDate(date: Date): string {
    return this.datePipe.transform(date, "dd/MM/yyyy") || "";
  }
}
