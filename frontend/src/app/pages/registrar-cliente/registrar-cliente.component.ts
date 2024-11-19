import { CommonModule } from "@angular/common";
import { Component, computed, inject, OnInit, signal } from "@angular/core";
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatRadioModule } from "@angular/material/radio";
import { SeleccionarUbicacionComponent } from "../../components/seleccionar-ubicacion/seleccionar-ubicacion.component";
import { PersonaService } from "../../services/persona.service";
import { EmpresaService } from "../../services/empresa.service";
import { UsuarioService } from "../../services/usuario.service";
import { MatSelectModule } from "@angular/material/select";
import { MatSnackBar } from "@angular/material/snack-bar";

import "moment/locale/es";
import { provideMomentDateAdapter } from "@angular/material-moment-adapter";
import { DateAdapter, MAT_DATE_LOCALE } from "@angular/material/core";
import { Router } from "@angular/router";
import { delay } from "rxjs";
import {
  MatDatepickerIntl,
  MatDatepickerModule,
} from "@angular/material/datepicker";

@Component({
  selector: "app-registrar-cliente",
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatRadioModule,
    CommonModule,
    ReactiveFormsModule,
    MatIconModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    SeleccionarUbicacionComponent,
    MatSelectModule,
    MatDatepickerModule,
  ],
  templateUrl: "./registrar-cliente.component.html",
  styleUrl: "./registrar-cliente.component.css",
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: "es-ES" },
    provideMomentDateAdapter(),
  ],
})
export class RegistrarClienteComponent implements OnInit {
  private readonly _adapter =
    inject<DateAdapter<unknown, unknown>>(DateAdapter);
  private readonly _intl = inject(MatDatepickerIntl);
  private readonly _locale = signal(inject<unknown>(MAT_DATE_LOCALE));
  readonly dateFormatString = computed(() => {
    if (this._locale() === "ja-JP") {
      return "YYYY/MM/DD";
    } else if (this._locale() === "fr") {
      return "DD/MM/YYYY";
    } else if (this._locale() === "es-ES") {
      return "DD/MM/YYYY";
    }
    return "";
  });

  ngOnInit() {
    this.spanish();
    this.updateCloseButtonLabel("Cerrar el calendario");
  }

  spanish() {
    this._locale.set("es-ES");
    this._adapter.setLocale(this._locale());
  }

  updateCloseButtonLabel(label: string) {
    this._intl.closeCalendarLabel = label;
    this._intl.changes.next();
  }

  selectedType: "persona" | "empresa" | null = null;

  personaForm: FormGroup;
  empresaForm: FormGroup;

  dniControl = new FormControl("");
  rucControl = new FormControl("");
  telefonoControl = new FormControl("");
  celularControl = new FormControl("");

  minDate: Date = new Date(1950, 0, 1);
  maxDate: Date = new Date(2006, 11, 31);

  constructor(
    private fb: FormBuilder,
    private personaService: PersonaService,
    private usuarioService: UsuarioService,
    private empresaService: EmpresaService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.personaForm = this.fb.group({
      dni: ["", [Validators.required, Validators.pattern(/^\d{8}$/)]],
      nombre: ["", Validators.required],
      apePaterno: ["", Validators.required],
      apeMaterno: ["", Validators.required],
      celular: [
        "",
        [
          Validators.required,
          Validators.pattern("^9\\d{8}$"),
          Validators.minLength(9),
        ],
      ],
      correo: ["", [Validators.required, Validators.email]],
      fechaNac: ["", Validators.required],
      genero: ["", Validators.required],
      direccion: ["", Validators.required],
      distrito: [null, Validators.required],
    });

    this.empresaForm = this.fb.group({
      ruc: ["", [Validators.required, Validators.pattern(/^\d{11}$/)]],
      razonSocial: ["", Validators.required],
      telefono: [
        "",
        [
          Validators.required,
          Validators.pattern("^9\\d{8}$"),
          Validators.minLength(9),
        ],
      ],
      direccion: ["", Validators.required],
      distrito: [null, Validators.required],
    });

    this.setupDniWatcher();
    this.setupRucWatcher();
  }

  protected onRucInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, "");
    this.rucControl.setValue(input.value);
  }

  protected onDniInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, "");
    this.dniControl.setValue(input.value);
  }

  protected onCelularInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, "");
    this.celularControl.setValue(input.value);
  }

  protected onTelefonoInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, "");
    this.telefonoControl.setValue(input.value);
  }

  valueDni(): string {
    return this.dniControl.value || "";
  }

  valueRuc(): string {
    return this.rucControl.value || "";
  }

  valueCelular(): string {
    return this.celularControl.value || "";
  }

  valueTelefono(): string {
    return this.telefonoControl.value || "";
  }

  validateDateInput(event: Event): void {
    const input = event.target as HTMLInputElement; // Obtén el elemento de entrada
    let value = input.value;

    // Permite solo números y '/'
    value = value.replace(/[^0-9/]/g, "");

    // Asegúrate de que siga el formato dd/mm/yyyy
    const parts = value.split("/");
    if (parts.length > 3) {
      parts.length = 3; // Limita a día/mes/año
    }

    // Verifica día (máximo 2 dígitos)
    if (parts[0]?.length > 2) {
      parts[0] = parts[0].substring(0, 2);
    }

    // Verifica mes (máximo 2 dígitos)
    if (parts[1]?.length > 2) {
      parts[1] = parts[1].substring(0, 2);
    }

    // Verifica año (máximo 4 dígitos)
    if (parts[2]?.length > 4) {
      parts[2] = parts[2].substring(0, 4);
    }

    // Reconstruye el valor validado
    input.value = parts.join("/");
  }

  selectType(type: "persona" | "empresa"): void {
    this.selectedType = type;
  }

  onDistritoSelected(distrito: any): void {
    if (this.selectedType === "persona") {
      this.personaForm.patchValue({ distrito: { id: distrito.id } });
    } else if (this.selectedType === "empresa") {
      this.empresaForm.patchValue({ distrito: { id: distrito.id } });
    }
  }

  vincularCliente(id: number) {
    const updateRequest = {
      clienteId: id,
      companiaId: null,
    };

    this.usuarioService
      .updateClientOrSede(updateRequest)
      .pipe(
        // Esperamos 2 segundos antes de hacer la redirección
        delay(2000)
      )
      .subscribe(() => {
        this.snackBar.open("Cliente vinculado con éxito", "cerrar");

        // Después de los 2 segundos, redirigimos a la ruta '/home'
        this.router.navigate(["/home"]);
      });
  }

  setupDniWatcher(): void {
    this.personaForm.get("dni")?.valueChanges.subscribe((dni) => {
      if (dni.length === 8 && /^\d{8}$/.test(dni)) {
        this.personaService.consultarDni(dni).subscribe(
          (data) => {
            this.autocompletarPersona(data);
            if (data.error) {
              this.limpiarPersona();
              this.snackBar.open(data.error, "Cerrar", {
                duration: 3000,
                verticalPosition: "top",
              });
            }
          },
          (error) => {
            this.limpiarPersona();
            this.snackBar.open(error.error?.error, "Cerrar", {
              duration: 3000,
              verticalPosition: "top",
            });
          }
        );
      }
    });
  }

  setupRucWatcher(): void {
    this.empresaForm.get("ruc")?.valueChanges.subscribe((ruc) => {
      if (ruc.length === 11 && /^\d{11}$/.test(ruc)) {
        this.empresaService.consultarRuc(ruc).subscribe(
          (data) => {
            console.log("Datos del RUC:", data);
            this.autocompletarEmpresa(data);
          },
          (error) => {
            console.error("Error al consultar el RUC:", error);
          }
        );
      }
    });
  }

  autocompletarPersona(data: any): void {
    this.personaForm.patchValue({
      nombre: data.nombres,
      apePaterno: data.apellidoPaterno,
      apeMaterno: data.apellidoMaterno,
    });
  }

  limpiarPersona(): void {
    this.personaForm.patchValue({
      nombre: "",
      apePaterno: "",
      apeMaterno: "",
    });
  }

  autocompletarEmpresa(data: any): void {
    this.empresaForm.patchValue({
      razonSocial: data.razonSocial,
      telefono: data.telefono || "", // Si el servicio devuelve un teléfono, lo autocompletamos
      direccion: data.direccion || "", // Si el servicio devuelve una dirección, la autocompletamos
    });
  }

  registrar(): void {
    if (this.selectedType === "persona" && this.personaForm.valid) {
      const personaData = { ...this.personaForm.value };

      console.log("p: ", personaData);

      this.personaService.crearPersona(personaData).subscribe((data) => {
        this.vincularCliente(data.clienteId);
      });
    } else if (this.selectedType === "empresa" && this.empresaForm.valid) {
      const empresaData = this.empresaForm.value;
      this.empresaService.createEmpresa(empresaData).subscribe((data) => {
        this.vincularCliente(data.clienteId);
      });
    }
  }
}
