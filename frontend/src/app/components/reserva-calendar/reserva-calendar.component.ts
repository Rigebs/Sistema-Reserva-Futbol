import { NgFor } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-reserva-calendar',
  standalone: true,
  imports: [NgFor],
  templateUrl: './reserva-calendar.component.html',
  styleUrl: './reserva-calendar.component.css'
})
export class ReservaCalendarComponent {
  meses: string[] = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
  diasSemana: string[] = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
  fechaSeleccionada: Date = new Date();
  horasDisponibles: string[] = [];
  horaSeleccionada: string = '';

  // Emitimos la fecha y hora seleccionada al componente padre
  @Output() fechaSeleccionadaEvent = new EventEmitter<{ fecha: Date, hora: string }>();

  // Variables para el mes y año actual
  mesActual: number = this.fechaSeleccionada.getMonth();
  anioActual: number = this.fechaSeleccionada.getFullYear();

  constructor() {
    this.generarHoras();
  }

  // Generar las horas disponibles (de 8:00 a 22:00)
  generarHoras() {
    this.horasDisponibles = [];
    for (let i = 8; i <= 22; i++) {
      this.horasDisponibles.push(`${i}:00`);
    }
  }

  // Cambiar el mes actual
  cambiarMes(incremento: number) {
    this.mesActual += incremento;
    if (this.mesActual < 0) {
      this.mesActual = 11;
      this.anioActual--;
    } else if (this.mesActual > 11) {
      this.mesActual = 0;
      this.anioActual++;
    }
  }

  // Seleccionar día del mes
  seleccionarDia(dia: number) {
    this.fechaSeleccionada = new Date(this.anioActual, this.mesActual, dia);
  }

  // Confirmar la selección de la hora
  seleccionarHora(hora: string) {
    this.horaSeleccionada = hora;
    this.fechaSeleccionadaEvent.emit({ fecha: this.fechaSeleccionada, hora: this.horaSeleccionada });
  }

  // Generar los días del mes
  obtenerDiasDelMes(): number[] {
    const inicioMes = new Date(this.anioActual, this.mesActual, 1).getDay();
    const diasEnMes = new Date(this.anioActual, this.mesActual + 1, 0).getDate();
    
    const dias: number[] = [];
    for (let i = 0; i < inicioMes; i++) {
      dias.push(0); // Días vacíos al inicio del mes
    }
    for (let i = 1; i <= diasEnMes; i++) {
      dias.push(i);
    }
    return dias;
  }
}
