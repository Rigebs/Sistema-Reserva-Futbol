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
import { MatSnackBar } from "@angular/material/snack-bar";

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

  precio: number = 0;

  horaInicio: number = 0;
  horaFinal: number = 0;

  isThereHours: boolean = true;

  isHoraFinalDisponible: boolean = true;

  @Output() reservaFinalizada = new EventEmitter<{
    campoId: number;
    campoNombre: string;
    fecha: Date;
    precio: number;
    horaInicio: string;
    horaFinal: string;
  }>();

  constructor(
    private _formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<ProcesoReservaComponent>,
    private availabilityCamposService: AvailabilityCamposService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.atenciónStartHour = parseInt(this.data.horaInicio.split(":")[0]);
    this.atenciónEndHour = parseInt(this.data.horaFinal.split(":")[0]);

    console.log("DESDE PROCESO: ", this.atenciónStartHour);

    const today = new Date();
    today.setDate(today.getDate() + 7);
    this.maxDate = today;
    this.firstFormGroup = this._formBuilder.group({
      fechaReserva: [
        "",
        [Validators.required, this.customValidator.bind(this)],
      ],
    });

    this.secondFormGroup = this._formBuilder.group({
      horaInicio: ["", Validators.required],
      horaFin: [
        { value: "", disabled: this.isHoraFinalDisponible },
        Validators.required,
      ],
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

    this.precio = this.data.reserva.precio;

    this.availableHours = this.availableHours.filter(
      (hour) => !this.unavailableHours.includes(hour)
    );
  }

  customValidator(control: any): { [key: string]: boolean } | null {
    if (!this.isThereHours) {
      return null;
    } else {
      return { noHour: true };
    }
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

          // Filtrar las horas disponibles
          this.availableHours = dataTime
            .map((hour) => parseInt(hour.split(":")[0]))
            .filter(
              (hour) =>
                hour >= this.atenciónStartHour && hour <= this.atenciónEndHour
            );

          // Si no hay horas disponibles después del filtrado
          if (!this.availableHours || this.availableHours.length === 0) {
            this.isThereHours = true; // No hay horas disponibles
            this.firstFormGroup.get("fechaReserva")?.updateValueAndValidity(); // Revalida el formulario
            this.snackBar.open(
              "No hay horas disponibles para esta fecha",
              "Cerrar",
              {
                duration: 3000,
                verticalPosition: "top",
                horizontalPosition: "center",
              }
            );
            return;
          }
          console.log("pasooo");

          this.isThereHours = false;
          this.firstFormGroup.get("fechaReserva")?.updateValueAndValidity(); // Revalida el formulario

          console.log(
            "Horas disponibles después de filtrar:",
            this.availableHours
          );

          // Filtrar horas de fin si ya se seleccionó una hora de inicio
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

      if (this.filteredEndHours && this.filteredEndHours.length > 0) {
        this.isHoraFinalDisponible = true; // Actualiza la variable booleana
        this.secondFormGroup.get("horaFin")?.enable(); // Habilita el control
      } else {
        this.isHoraFinalDisponible = false;
        this.secondFormGroup.get("horaFin")?.disable(); // Deshabilita el control si no hay horas válidas
        this.snackBar.open(
          "No hay horas disponibles para continuar",
          "Cerrar",
          {
            duration: 3000,
            verticalPosition: "top",
            horizontalPosition: "center",
          }
        );
      }

      this.secondFormGroup.patchValue({ horaFin: "" }); // Resetea el valor de horaFin
      this.horaInicio = selectedHour;

      console.log("Horas disponibles para hora fin:", this.filteredEndHours);
    } else {
      // Actualiza el valor de hora final y calcula el precio
      this.horaFinal = selectedHour;
      this.precio =
        this.data.reserva.precio * (this.horaFinal - this.horaInicio);
      this.secondFormGroup.patchValue({ horaFin: selectedHour });
    }
  }

  completeReserva() {
    const horaInicioString = `${this.secondFormGroup.value.horaInicio}:00`;
    const horaFinString = `${this.secondFormGroup.value.horaFin}:00`;

    const reservaData = {
      campoId: this.data.reserva.id,
      campoNombre: this.data.reserva.nombre,
      fecha: this.firstFormGroup.value.fechaReserva,
      horaInicio: horaInicioString,
      horaFinal: horaFinString,
      precio: this.precio,
    };

    this.reservaFinalizada.emit(reservaData);
    console.log("EMIT: ", reservaData);

    this.dialogRef.close();
  }

  formatHour(hour: number): string {
    const suffix = hour >= 12 ? "PM" : "AM";
    const formattedHour = hour % 12 || 12;
    return `${formattedHour}:00 ${suffix}`;
  }
}
