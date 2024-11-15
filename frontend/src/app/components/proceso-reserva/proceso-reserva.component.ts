import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";
import { NgForOf, NgIf } from "@angular/common";
import {
  Component,
  EventEmitter,
  Inject,
  Input,
  OnInit,
  Output,
} from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import {
  MatDatepickerInputEvent,
  MatDatepickerModule,
} from "@angular/material/datepicker";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatRadioButton, MatRadioGroup } from "@angular/material/radio";
import { MatStepperModule } from "@angular/material/stepper";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import {
  MatOptionModule,
  MatOptionSelectionChange,
} from "@angular/material/core";
import { MatSelectChange, MatSelectModule } from "@angular/material/select";

@Component({
  selector: "app-proceso-reserva",
  standalone: true,
  imports: [
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    MatDialogModule,
    MatButtonModule,
    MatDatepickerModule,
    MatOptionModule,
    MatSelectModule,
    NgForOf,
    ReactiveFormsModule,
  ],
  templateUrl: "./proceso-reserva.component.html",
  styleUrls: ["./proceso-reserva.component.css"],
})
export class ProcesoReservaComponent implements OnInit {
  isLinear = true;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  availableHours: number[] = [];
  filteredEndHours: number[] = [];
  minDate: Date | undefined;

  atenciónStartHour = 8; // 8 AM
  atenciónEndHour = 22; // 10 PM
  unavailableRanges: { start: number; end: number }[] = [
    { start: 12, end: 14 }, // Ejemplo: Bloqueando de 12 a 14
    { start: 18, end: 19 }, // Ejemplo: Bloqueando de 18 a 19
  ];

  unavailableHours: number[] = [];

  @Output() reservaFinalizada = new EventEmitter<{
    id: number;
    nombre: string;
    fecha: Date;
    horaInicio: string;
    horaFin: string;
  }>();

  constructor(
    private _formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<ProcesoReservaComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.firstFormGroup = this._formBuilder.group({
      fechaReserva: ["", Validators.required],
    });

    this.secondFormGroup = this._formBuilder.group({
      horaInicio: ["", Validators.required],
      horaFin: ["", Validators.required],
    });
  }

  cerrarDialogo(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.minDate = new Date();
    this.availableHours = Array.from(
      { length: this.atenciónEndHour - this.atenciónStartHour + 1 },
      (_, i) => this.atenciónStartHour + i
    );
    this.unavailableHours = this.getUnavailableHours();
    this.availableHours = this.availableHours.filter(
      (hour) => !this.unavailableHours.includes(hour)
    );
  }

  getUnavailableHours(): number[] {
    const hours: number[] = [];
    this.unavailableRanges.forEach((range) => {
      for (let i = range.start; i < range.end; i++) {
        hours.push(i);
      }
    });
    return hours;
  }

  onHourSelected(event: MatSelectChange, isStartHour: boolean) {
    const selectedHour = event.value;

    if (isStartHour) {
      this.secondFormGroup.patchValue({ horaInicio: selectedHour });
      this.filteredEndHours = this.availableHours.filter(
        (hour) => hour > selectedHour
      );
      this.secondFormGroup.patchValue({ horaFin: "" });
    } else {
      this.secondFormGroup.patchValue({ horaFin: selectedHour });
    }
  }

  completeReserva() {
    const horaInicioString = `${this.secondFormGroup.value.horaInicio}:00`;
    const horaFinString = `${this.secondFormGroup.value.horaFin}:00`;

    const reservaData = {
      id: this.data.id, // Asegúrate de pasar el ID del campo
      nombre: this.data.nombre,
      fecha: this.firstFormGroup.value.fechaReserva,
      horaInicio: horaInicioString,
      horaFin: horaFinString,
      precio: this.data.precio,
    };

    this.reservaFinalizada.emit(reservaData);
    this.dialogRef.close();
  }

  formatHour(hour: number): string {
    const suffix = hour >= 12 ? "PM" : "AM";
    const formattedHour = hour % 12 || 12;
    return `${formattedHour}:00 ${suffix}`;
  }
}
