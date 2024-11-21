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
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatStepperModule } from "@angular/material/stepper";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatOptionModule } from "@angular/material/core";
import { MatSelectChange, MatSelectModule } from "@angular/material/select";
import { CommonModule } from "@angular/common";
import { AvailabilityCamposService } from "../../services/availability-campos.service";

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
    ReactiveFormsModule,
    CommonModule,
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
  maxDate: Date | undefined;

  atenciónStartHour: number;
  atenciónEndHour: number;
  unavailableRanges: { start: number; end: number }[] = [
    { start: 12, end: 14 },
    { start: 18, end: 19 },
  ];

  id: number | undefined;

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
    private availabilityCamposService: AvailabilityCamposService,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.atenciónStartHour = parseInt(this.data.horaInicio.split(":")[0]);
    this.atenciónEndHour = parseInt(this.data.horaFinal.split(":")[0]);

    console.log("DESDE PROCESO: ", this.atenciónStartHour);

    const today = new Date();
    today.setDate(today.getDate() + 7);
    this.maxDate = today;
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
    this.id = this.data.reserva.id;
    this.minDate = new Date();
    const today = new Date();
    today.setDate(today.getDate() + 7);
    this.maxDate = today;

    this.availableHours = this.availableHours.filter(
      (hour) => !this.unavailableHours.includes(hour)
    );
  }

  onDateChange(event: any): void {
    if (this.id !== undefined) {
      const selectedDate = new Date(event.value);
      const formattedDate = selectedDate.toISOString().split("T")[0];
      console.log("Fecha seleccionada:", formattedDate);

      this.availabilityCamposService
        .getAvailableHours(this.id, formattedDate)
        .subscribe((dataTime: string[]) => {
          console.log("Horas disponibles del backend:", dataTime);

          this.availableHours = dataTime
            .map((hour) => parseInt(hour.split(":")[0]))
            .filter(
              (hour) =>
                hour >= this.atenciónStartHour && hour <= this.atenciónEndHour
            );

          console.log(
            "Horas disponibles después de filtrar:",
            this.availableHours
          );

          if (this.secondFormGroup.value.horaInicio) {
            this.filteredEndHours = this.availableHours.filter(
              (hour) => hour > this.secondFormGroup.value.horaInicio
            );
          }
        });
    } else {
      console.error("El id no está definido");
    }
  }

  onHourSelected(event: MatSelectChange, isStartHour: boolean) {
    const selectedHour = event.value;

    if (isStartHour) {
      this.secondFormGroup.patchValue({ horaInicio: selectedHour });

      const nextHour = selectedHour + 1;
      this.filteredEndHours = this.availableHours.filter(
        (hour) =>
          hour === nextHour ||
          hour === nextHour + 1 ||
          hour === nextHour + 2 ||
          hour === nextHour + 3
      );

      console.log("FILTERED: ", this.filteredEndHours);

      // Resetear la hora de fin
      this.secondFormGroup.patchValue({ horaFin: "" });
    } else {
      this.secondFormGroup.patchValue({ horaFin: selectedHour });
    }
  }

  completeReserva() {
    const horaInicioString = `${this.secondFormGroup.value.horaInicio}:00`;
    const horaFinString = `${this.secondFormGroup.value.horaFin}:00`;

    const reservaData = {
      id: this.data.reserva.id, // Asegúrate de pasar el ID del campo
      nombre: this.data.reserva.nombre,
      fecha: this.firstFormGroup.value.fechaReserva,
      horaInicio: horaInicioString,
      horaFin: horaFinString,
      precio: this.data.reserva.precio,
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
