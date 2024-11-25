import { Component, OnInit } from "@angular/core";
import { MatInputModule } from "@angular/material/input";
import { MatStepperModule } from "@angular/material/stepper";
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { CommonModule } from "@angular/common";
import { MatButtonModule } from "@angular/material/button";
import { MatOptionModule } from "@angular/material/core";
import { MatSelectModule } from "@angular/material/select";
import { MatIconModule } from "@angular/material/icon";
import { Departamento } from "../../models/departamento";
import { Provincia } from "../../models/provincia";
import { Distrito } from "../../models/distrito";
import { DepartamentoService } from "../../services/departamento.service";
import { ProvinciaService } from "../../services/provincia.service";
import { DistritoService } from "../../services/distrito.service";
import { EmpresaService } from "../../services/empresa.service";
import { CompaniaService } from "../../services/compania.service";
import { UsuarioService } from "../../services/usuario.service";
import { Router } from "@angular/router";
import { AuthService } from "../../services/auth.service";
import { MatSnackBar } from "@angular/material/snack-bar";
import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";
import { IntroDialogComponent } from "../../components/intro-dialog/intro-dialog.component";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";

@Component({
  selector: "app-registrar-sede",
  standalone: true,
  imports: [
    MatInputModule,
    MatStepperModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatOptionModule,
    MatSelectModule,
    MatIconModule,
    MatDialogModule,
  ],
  templateUrl: "./registrar-sede.component.html",
  styleUrl: "./registrar-sede.component.css",
})
export class RegistrarSedeComponent implements OnInit {
  orientation: "horizontal" | "vertical" = "horizontal";
  empresaFormGroup!: FormGroup; // Asegúrate de que estas variables estén correctamente tipadas
  companiaFormGroup!: FormGroup;
  departamentos: Departamento[] = [];
  provincias: Provincia[] = [];
  distritos: Distrito[] = [];
  filteredProvincias: Provincia[] = [];
  filteredDistritos: Distrito[] = [];

  isMobile: boolean = false;

  isProvinciaAvalaible: boolean = false;
  isDistritoAvalaible: boolean = false;

  companyRegistered: boolean = false;

  file: File | null = null;
  qrFile: File | null = null;

  rucControl = new FormControl("");
  telefonoControl = new FormControl("");
  yapeControl = new FormControl("");

  availableStartHours: number[] = [
    5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
  ];
  filteredEndHours: number[] = [];

  fileName: string | null = null;

  qrFileName: string | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private departamentoService: DepartamentoService,
    private provinciaService: ProvinciaService,
    private distritoService: DistritoService,
    private empresaService: EmpresaService,
    private companiaService: CompaniaService,
    private usuarioService: UsuarioService,
    private router: Router,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private breakpointObserver: BreakpointObserver,
    private dialog: MatDialog
  ) {}

  protected onRucInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, "");
    this.rucControl.setValue(input.value);
  }

  protected onTelefonoInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, "");
    this.telefonoControl.setValue(input.value);
  }

  protected onYapeInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, "");
    this.yapeControl.setValue(input.value);
  }

  valueYape(): string {
    return this.yapeControl.value || "";
  }

  value(): string {
    return this.rucControl.value || "";
  }

  valueTelefono(): string {
    return this.telefonoControl.value || "";
  }

  ngOnInit(): void {
    this.checkUserRole();
    this.empresaFormGroup = this.formBuilder.group({
      ruc: ["", [Validators.required, Validators.maxLength(11)]],
      razonSocial: ["", [Validators.maxLength(100)]],
      telefono: [
        "",
        [
          Validators.required,
          Validators.pattern("^9\\d{8}$"),
          Validators.minLength(9),
        ],
      ],
      direccion: [""],
      departamento: [null, Validators.required],
      provincia: [{ value: null, disabled: true }, Validators.required], // Inicialmente deshabilitado
      distrito: [{ value: null, disabled: true }, Validators.required], // Inicialmente deshabilitado
    });

    this.companiaFormGroup = this.formBuilder.group({
      nombre: ["", Validators.required],
      concepto: ["", Validators.required],
      correo: ["", [Validators.required, Validators.email]],
      yape: [
        "",
        [
          Validators.required, // Aseguramos que el campo es obligatorio
          Validators.pattern("^9\\d{8}$"),
          Validators.minLength(9),
        ],
      ],
      pagWeb: [""],
      imagenSede: [null, Validators.required], // Imagen de sede requerida
      imagenQrYape: [null, Validators.required], // QR de Yape requerido
      horaInicio: ["", [Validators.required]],
      horaFin: ["", [Validators.required]],
    });

    this.cargarDepartamentos();

    this.empresaFormGroup
      .get("ruc")
      ?.valueChanges.subscribe((rucValue: string) => {
        if (rucValue.length === 11) {
          this.consultarReniec();
        }
      });
    this.breakpointObserver
      .observe([Breakpoints.Handset])
      .subscribe((result) => {
        this.isMobile = result.matches;
      });
  }

  convertTo12HourFormat(hour: number): string {
    const suffix = hour >= 12 ? "PM" : "AM";
    const hourIn12 = hour % 12 || 12; // Convertir hora a formato 12h
    return `${hourIn12}:00 ${suffix}`;
  }

  // Método para filtrar las horas de fin basadas en la hora de inicio
  onStartHourChange(event: any): void {
    const startHour = event.value; // Obtener la hora de inicio seleccionada

    // Filtrar las horas de fin para que solo estén disponibles aquellas horas mayores que la hora de inicio más 8 horas
    this.filteredEndHours = this.availableStartHours.filter(
      (hour) => hour >= startHour + 8
    );

    // Si ya hay una hora de fin seleccionada y no está en el rango filtrado, resetear la hora de fin
    if (
      this.companiaFormGroup.get("horaFin")?.value &&
      this.companiaFormGroup.get("horaFin")?.value < startHour + 8
    ) {
      this.companiaFormGroup.get("horaFin")?.setValue(null);
    }
  }

  onFileSelected(event: any, tipo: "imagenSede" | "imagenQrYape") {
    const file = event.target.files[0];

    if (tipo === "imagenSede") {
      this.file = file; // Guardar el archivo en la variable correspondiente
      this.fileName = file.name; // Almacenar el nombre del archivo
      this.companiaFormGroup.get("imagenSede")?.setValue(file);
    } else if (tipo === "imagenQrYape") {
      this.qrFile = file; // Guardar el archivo en la variable correspondiente
      this.qrFileName = file.name; // Almacenar el nombre del archivo
      this.companiaFormGroup.get("imagenQrYape")?.setValue(file);
    }
  }

  cargarDepartamentos() {
    this.departamentoService.getAll().subscribe((data) => {
      this.departamentos = data;
    });
  }

  onDepartamentoChange() {
    const departamentoId = this.empresaFormGroup.get("departamento")?.value;

    // Si el departamento ha sido seleccionado, habilitamos la provincia
    if (departamentoId) {
      this.provinciaService
        .getByDepartamentoId(departamentoId)
        .subscribe((data) => {
          this.filteredProvincias = data;
          this.isProvinciaAvalaible = true;
          this.filteredDistritos = [];
          this.empresaFormGroup.get("provincia")?.setValue(null);
          this.empresaFormGroup.get("distrito")?.setValue(null);

          // Habilitar provincia
          this.empresaFormGroup.get("provincia")?.enable();
          this.empresaFormGroup.get("distrito")?.disable(); // Deshabilitar distrito hasta que se seleccione provincia
        });
    } else {
      this.isProvinciaAvalaible = false;
      this.isDistritoAvalaible = false;
      this.filteredProvincias = [];
      this.filteredDistritos = [];

      // Deshabilitar tanto provincia como distrito
      this.empresaFormGroup.get("provincia")?.disable();
      this.empresaFormGroup.get("distrito")?.disable();
    }
  }

  onProvinciaChange() {
    const provinciaId = this.empresaFormGroup.get("provincia")?.value;

    // Si la provincia ha sido seleccionada, habilitamos el distrito
    if (provinciaId) {
      this.distritoService.getByProvinciaId(provinciaId).subscribe((data) => {
        this.filteredDistritos = data;
        this.isDistritoAvalaible = true;
        this.empresaFormGroup.get("distrito")?.setValue(null);

        // Habilitar distrito
        this.empresaFormGroup.get("distrito")?.enable();
      });
    } else {
      // Si no se ha seleccionado una provincia, deshabilitamos el distrito
      this.isDistritoAvalaible = false;
      this.filteredDistritos = [];
      this.empresaFormGroup.get("distrito")?.setValue(null);

      // Deshabilitar distrito
      this.empresaFormGroup.get("distrito")?.disable();
    }
  }

  checkUserRole() {
    if (this.authService.hasRole("ROLE_COMPANIA")) {
      this.snackBar.open("Ya tienes registrada tu compañía", "Cerrar", {
        duration: 3000, // Duración de 3 segundos
        verticalPosition: "top",
      });
      this.router.navigate(["/home"]); // Redirigir al usuario a /home
    }
  }

  consultarReniec() {
    const ruc = this.empresaFormGroup.get("ruc")?.value;

    const rucValido = /^\d{11}$/.test(ruc);
    if (!rucValido) {
      this.snackBar.open("El RUC debe ser un número de 11 dígitos.", "Cerrar", {
        duration: 3000,
        verticalPosition: "top",
      });
      return;
    }

    this.empresaService.consultarRuc(ruc).subscribe(
      (response) => {
        if (response && response.razonSocial) {
          this.empresaFormGroup
            .get("razonSocial")
            ?.setValue(response.razonSocial);
          this.empresaFormGroup.get("direccion")?.setValue(response.direccion);

          this.snackBar.open("Datos encontrados correctamente.", "Cerrar", {
            duration: 3000,
            verticalPosition: "top",
          });
        } else {
          const mensajeError =
            response.error || "No se encontraron datos para este RUC.";
          this.snackBar.open(mensajeError, "Cerrar", {
            duration: 3000,
            verticalPosition: "top",
          });
        }
      },
      (error) => {
        const mensajeError =
          error.error?.error || "Ocurrió un error al consultar el RUC.";
        this.snackBar.open(mensajeError, "Cerrar", {
          duration: 3000,
          verticalPosition: "top",
        });
      }
    );
  }

  submitForm() {
    const empresaFormValue = this.empresaFormGroup.value;
    const distritoId = empresaFormValue.distrito;
    const distrito = { id: distritoId };

    const empresa = {
      ruc: empresaFormValue.ruc,
      razonSocial: empresaFormValue.razonSocial,
      telefono: empresaFormValue.telefono,
      direccion: empresaFormValue.direccion,
      distrito: distrito,
    };

    this.empresaService.createEmpresa(empresa).subscribe((empresaData) => {
      let horaInicio = this.companiaFormGroup.get("horaInicio")?.value;
      let horaFin = this.companiaFormGroup.get("horaFin")?.value;

      console.log("HORA INICIO: ", horaInicio, " hora fin: ", horaFin);

      // Convertir las horas a formato HH:MM:SS
      const formatToHHMMSS = (hour: number): string => {
        const hours = hour < 10 ? `0${hour}` : `${hour}`;
        return `${hours}:00:00`; // Asume minutos y segundos como 00
      };

      // Aplicamos el formato HH:MM:SS a horaInicio y horaFin
      horaInicio = formatToHHMMSS(horaInicio);
      horaFin = formatToHHMMSS(horaFin);

      const compania = {
        nombre: this.companiaFormGroup.get("nombre")?.value,
        concepto: this.companiaFormGroup.get("concepto")?.value,
        correo: this.companiaFormGroup.get("correo")?.value,
        celular: this.companiaFormGroup.get("yape")?.value,
        horaInicio: horaInicio, // Enviar en formato HH:MM:SS
        horaFin: horaFin, // Enviar en formato HH:MM:SS
        empresa: { id: empresaData.empresaId },
      };

      console.log("COMPANIA DATA: ", compania);

      this.companiaService
        .saveCompania(this.qrFile!, this.file!, compania)
        .subscribe((companiaData) => {
          const updateRequest = {
            clienteId: null,
            companiaId: companiaData.id,
          };

          this.usuarioService
            .updateClientOrSede(updateRequest)
            .subscribe((response) => {
              console.log("Relación cliente-sede actualizada:", response);
              this.companyRegistered = true;
            });
        });
    });
  }

  openDialog(): void {
    this.dialog.open(IntroDialogComponent, {
      width: "400px",
      autoFocus: false,
      disableClose: false,
    });
  }

  goToHome() {
    this.router.navigate(["/home"]);
  }
}
