import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { Router } from '@angular/router';
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { CommonModule, DatePipe } from '@angular/common';

@Component({
  selector: 'app-pasarela-pago',
  standalone: true,
  imports: [MatCardModule, MatTableModule, 
    CommonModule,
    MatButtonModule, NavbarComponent],
  templateUrl: './pasarela-pago.component.html',
  styleUrl: './pasarela-pago.component.css',
  providers: [DatePipe]
})
export class PasarelaPagoComponent {
  reservas: any[] = [];
  total: number = 0;

  constructor(private router: Router, private datePipe: DatePipe) {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state as { reservas?: any[] };

    if (state?.reservas) {
      this.reservas = state.reservas.map(reserva => ({
        ...reserva,
        horaInicio: this.convertirAHora(reserva.horaInicio),
        horaFin: this.convertirAHora(reserva.horaFin)
      }));
      this.calcularTotal();
    } else {
      this.router.navigate(['/reservar-campo']);
    }
  }

  // Convierte las horas en cadenas a objetos Date
  convertirAHora(hora: any): Date {
    if (typeof hora !== 'string') {
      console.error(`Hora inválida: ${hora}`);
      return new Date(); // O retornar null si prefieres
    }
    
    const [hours, minutes] = hora.split(':');
    const fecha = new Date();
    fecha.setHours(parseInt(hours, 10), parseInt(minutes, 10), 0);
    return fecha;
  }

  calcularTotal() {
    this.total = this.reservas.reduce((acc, reserva) => acc + reserva.precio, 0);
  }

  pagarConYape() {
    alert('Pago realizado con éxito con Yape.');
  }

  formatDate(date: Date): string {
    return this.datePipe.transform(date, 'dd/MM/yyyy') || '';
  }
}