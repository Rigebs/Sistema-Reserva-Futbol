import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { NgForOf, NgIf } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerInputEvent, MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatStepperModule } from '@angular/material/stepper';
import { DatePipe } from '@angular/common'; // Para formatear las fechas

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
    MatButtonModule,
    MatDatepickerModule,
    MatRadioGroup,
    NgForOf,
    MatRadioButton,
    ReactiveFormsModule
  ],
  templateUrl: './proceso-reserva.component.html',
  styleUrls: ['./proceso-reserva.component.css'],
  providers: [DatePipe] // Proveedor para el formateo de fechas
})
export class ProcesoReservaComponent implements OnInit {
  
  isLinear = true;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  selectedDates: string[] = []; // Almacenar las fechas formateadas como strings
  selectedHour: number = 12;
  handTransform: string = 'rotate(0deg)';
  hours: Hour[] = [];
  stepperOrientation: 'horizontal' | 'vertical' = 'horizontal';

  constructor(
    private _formBuilder: FormBuilder, 
    private breakpointObserver: BreakpointObserver,
    private datePipe: DatePipe // Para formatear las fechas seleccionadas
  ) {
    // Inicializar los formularios
    this.firstFormGroup = this._formBuilder.group({
      fechaReserva: ['', Validators.required],
    });

    this.secondFormGroup = this._formBuilder.group({
      horaReserva: ['', Validators.required],
    });

    // Inicializar las horas del reloj
    this.initializeHours();
  }

  ngOnInit(): void {
    // Cambiar la orientación del *stepper* según el tamaño de la pantalla
    this.breakpointObserver.observe([Breakpoints.Handset]).subscribe(result => {
      this.stepperOrientation = result.matches ? 'vertical' : 'horizontal';
    });
  }

  // Manejar la selección de la fecha
  onDateSelected(event: MatDatepickerInputEvent<Date>) {
    const selectedDate = event.value;
    if (selectedDate) {
      const formattedDate = this.datePipe.transform(selectedDate, 'dd/MM/yyyy');
      if (formattedDate && !this.selectedDates.includes(formattedDate)) {
        this.selectedDates.push(formattedDate);
      }
    }
  }

  // Manejar la selección de la hora
  onHourClick(hour: number) {
    this.selectedHour = hour;
    this.secondFormGroup.patchValue({ horaReserva: hour });
    const angle = (hour / 12) * 360 + 90;
    this.handTransform = `rotate(${angle}deg)`;
  }

  // Inicializar las posiciones de las horas en el reloj
  initializeHours() {
    const clockRadius = 80; // Radio del reloj
    const clockCenter = 100; // Centro del reloj (mitad del tamaño del reloj)
  
    for (let i = 1; i <= 12; i++) {
      const angle = (i / 12) * 360 - 90;
      const radian = (angle - 90) * (Math.PI / 180); // Ajustar para que empiece en las 12 en punto
  
      const x = clockCenter + clockRadius * Math.cos(radian) - 15; // 15 es la mitad del ancho de .clock-hour
      const y = clockCenter + clockRadius * Math.sin(radian) - 15; // 15 es la mitad del alto de .clock-hour
  
      const transform = `translate(${x}px, ${y}px)`;
  
      this.hours.push({
        value: i,
        display: i,
        transform: transform,
      });
    }
  }
}