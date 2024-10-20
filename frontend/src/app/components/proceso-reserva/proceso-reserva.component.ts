import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { NgForOf, NgIf } from '@angular/common';
import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerInputEvent, MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatStepperModule } from '@angular/material/stepper';
import { DatePipe } from '@angular/common'; // Para formatear las fechas
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatOptionModule, MatOptionSelectionChange } from '@angular/material/core';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';

interface Hour {
  value: number;
  display: number;
  transform: string;
}

@Component({
  selector: 'app-proceso-reserva',
  standalone: true,
  imports: [
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    NgIf,
    MatDialogModule,
    MatButtonModule,
    MatDatepickerModule,
    MatRadioGroup,
    MatOptionModule,
    MatSelectModule,
    NgForOf,
    MatRadioButton,
    ReactiveFormsModule
  ],
  templateUrl: './proceso-reserva.component.html',
  styleUrls: ['./proceso-reserva.component.css']
})
export class ProcesoReservaComponent {
  isLinear = true;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  availableHours: number[] = [];
  filteredEndHours: number[] = [];
  minDate: Date | undefined;
  
  // Rango de atención disponible
  atenciónStartHour = 8; // 8 AM
  atenciónEndHour = 22; // 10 PM
  unavailableRanges: { start: number, end: number }[] = [
    { start: 12, end: 14 }, // Ejemplo: Bloqueando de 12 a 14
    { start: 18, end: 19 }  // Ejemplo: Bloqueando de 18 a 19
  ];
  
  // Aquí está la propiedad que debe existir
  unavailableHours: number[] = [];

  @Output() reservaFinalizada = new EventEmitter<{ nombre: string, fecha: Date, horaInicio: string, horaFin: string }>();

  constructor(
    private _formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<ProcesoReservaComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.firstFormGroup = this._formBuilder.group({
      fechaReserva: ['', Validators.required],
    });

    this.secondFormGroup = this._formBuilder.group({
      horaInicio: ['', Validators.required],
      horaFin: ['', Validators.required],
    });
  }

  cerrarDialogo(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.minDate = new Date();
    // Inicializar las horas dentro del rango de atención
    this.availableHours = Array.from({ length: this.atenciónEndHour - this.atenciónStartHour }, (_, i) => this.atenciónStartHour + i);

    // Filtrar las horas disponibles excluyendo los rangos bloqueados
    this.unavailableHours = this.getUnavailableHours();
    this.availableHours = this.availableHours.filter(hour => !this.unavailableHours.includes(hour));
  }

  // Obtener las horas no disponibles basadas en los rangos
  getUnavailableHours(): number[] {
    const hours: number[] = [];
    this.unavailableRanges.forEach(range => {
      for (let i = range.start; i < range.end; i++) {
        hours.push(i);
      }
    });
    return hours;
  }

  // Manejar la selección de la hora de inicio
  onHourSelected(event: MatSelectChange, isStartHour: boolean) {
    const selectedHour = event.value;
    
    if (isStartHour) {
      // Establecer la hora de inicio
      this.secondFormGroup.patchValue({ horaInicio: selectedHour });

      // Filtrar las horas de fin, deben ser mayores que la hora de inicio seleccionada
      this.filteredEndHours = this.availableHours.filter(hour => hour > selectedHour);
      
      // Limpiar la selección de hora de fin
      this.secondFormGroup.patchValue({ horaFin: '' });
    } else {
      // Establecer la hora de fin
      this.secondFormGroup.patchValue({ horaFin: selectedHour });
    }
  }

  completeReserva() {
    const reservaData = {
      nombre: this.data.nombreCampo,
      fecha: this.firstFormGroup.value.fechaReserva,
      horaInicio: this.secondFormGroup.value.horaInicio,
      horaFin: this.secondFormGroup.value.horaFin,
    };

    // Emitir el evento con los detalles de la reserva
    this.reservaFinalizada.emit(reservaData);

    // Cerrar el diálogo
    this.dialogRef.close();
  }
}