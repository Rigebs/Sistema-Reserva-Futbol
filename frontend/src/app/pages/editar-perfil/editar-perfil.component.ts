import { Component } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { MatInputModule } from "@angular/material/input";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatSelectModule } from "@angular/material/select";
import { CommonModule } from "@angular/common";
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";
import { UsuarioService } from "../../services/usuario.service";
import { UserDetails } from "../../models/user-details";
import { AuthTokenUtil } from "../../utils/auth-token-util";
import { Compania } from "../../models/compania";
import { Cliente } from "../../models/cliente";
import { Persona } from "../../models/persona";
import { Empresa } from "../../models/empresa";
import { CompaniaService } from "../../services/compania.service";
import { DistritoService } from "../../services/distrito.service";
import { Distrito } from "../../models/distrito";
import { Provincia } from "../../models/provincia";
import { Departamento } from "../../models/departamento";
import { ProvinciaService } from "../../services/provincia.service";
import { DepartamentoService } from "../../services/departamento.service";
import { MatIconModule } from "@angular/material/icon";
import { ActivatedRoute, Router } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: "app-editar-perfil",
  standalone: true,
  imports: [
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    ReactiveFormsModule,
    CommonModule,
    MatCardModule,
    MatButtonModule,
    FormsModule,
    MatIconModule,
  ],
  templateUrl: "./editar-perfil.component.html",
  styleUrl: "./editar-perfil.component.css",
})
export class EditarPerfilComponent {
  profileForm!: FormGroup;
  userData!: UserDetails;
  roleCliente: boolean = false;
  roleCompania: boolean = false;

  cargado: boolean = false;

  availableStartHours: number[] = [
    5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
  ];
  filteredEndHours: number[] = [];

  file: File | null = null;
  qrFile: File | null = null;

  fileName: string | null = null;

  qrFileName: string | null = null;

  imagePreview: string | null = null;
  qrPreview: string | null = null;

  isImageSelected: boolean = false;
  isQrSelected: boolean = false;

  distritos: Distrito[] = [];
  provincias: Provincia[] = [];
  departamentos: Departamento[] = [];

  departamentoSeleccionado: number | undefined;
  provinciaSeleccionado: number | undefined;
  distritoSeleccionado: number | undefined;

  provinciaHabilitado: boolean = true;
  distritoHabilitado: boolean = true;

  ubicacionCargada: boolean = false;

  rutaAnterior: string | undefined;

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private authTokenUtil: AuthTokenUtil,
    private companiaService: CompaniaService,
    private distritoService: DistritoService,
    private provinciaService: ProvinciaService,
    private departamentoService: DepartamentoService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.profileForm = this.fb.group({});

    this.getUserData();

    this.route.queryParams.subscribe((params) => {
      this.rutaAnterior = params["from"];
      console.log("RUTA: ", params["from"]);
    });
  }

  initForm() {
    console.log("UISER DATA: ", this.userData);

    if (this.roleCliente) {
      this.cargarDistritos();
      console.log("DIST: ", this.distritos);

      this.profileForm = this.fb.group({
        username: [this.userData.username, Validators.required],
        email: [this.userData.email, [Validators.required, Validators.email]],
        dni: [this.userData.cliente!.persona!.dni, Validators.required],
        nombre: [this.userData.cliente?.persona?.nombre, Validators.required],
        apePaterno: [
          this.userData.cliente!.persona!.apePaterno,
          Validators.required,
        ],
        apeMaterno: [
          this.userData.cliente!.persona!.apeMaterno,
          Validators.required,
        ],
        celular: [
          this.userData.cliente!.persona!.celular,
          [Validators.required, Validators.pattern(/^\d{9}$/)],
        ],
        correo: [
          this.userData.cliente!.persona!.correo,
          [Validators.required, Validators.email],
        ],
        fechaNac: [
          this.userData.cliente!.persona!.fechaNac,
          Validators.required,
        ],
        genero: [
          this.userData.cliente!.persona!.genero === "M"
            ? "Masculino"
            : "Femenino",
          Validators.required,
        ],
        direccion: [
          this.userData.cliente!.persona!.direccion,
          Validators.required,
        ],
        departamento: [
          this.userData.cliente?.persona?.distrito?.provincia?.departamento.id,
          Validators.required,
        ],
        provincia: [
          this.userData.cliente?.persona?.distrito?.provincia?.id,
          Validators.required,
        ],
        distrito: [
          this.userData.cliente?.persona?.distrito?.id,
          Validators.required,
        ],
      });
    } else if (this.roleCompania) {
      this.onStartForm(this.extractHour(this.userData.compania!.horaInicio!));
      this.profileForm = this.fb.group({
        username: [this.userData.username, Validators.required],
        email: [this.userData.email, [Validators.required, Validators.email]],
        nombre: [this.userData.compania!.nombre, Validators.required],
        concepto: [this.userData.compania!.concepto, Validators.required],
        celular: [
          this.userData.compania!.celular,
          [Validators.required, Validators.pattern(/^\d{9}$/)],
        ],
        correo: [
          this.userData.compania!.correo,
          [Validators.required, Validators.email],
        ],
        horaInicio: [
          this.extractHour(this.userData.compania!.horaInicio!),
          Validators.required,
        ],
        horaFin: [
          this.extractHour(this.userData.compania!.horaFin!),
          Validators.required,
        ],
        imagenSede: [null, Validators.required],
        imagenQrYape: [null, Validators.required],
        ruc: [this.userData.empresa!.ruc, Validators.required],
        razonSocial: [this.userData.empresa!.razonSocial, Validators.required],
        telefono: [
          this.userData.empresa!.telefono,
          [Validators.required, Validators.pattern(/^\d+$/)],
        ],
        direccion: [this.userData.empresa!.direccion, Validators.required],
        departamento: [
          this.userData.cliente?.persona?.distrito?.provincia?.departamento.id,
          Validators.required,
        ],
        provincia: [
          this.userData.cliente?.persona?.distrito?.provincia?.id,
          Validators.required,
        ],
        distrito: [
          this.userData.cliente?.persona?.distrito?.id,
          Validators.required,
        ],
      });

      this.imagePreview = this.userData.compania?.imagen?.imageUrl!;
      this.qrPreview = this.userData.compania?.qrImagen?.imageUrl!;
    }
  }

  convertGeneroToBackend(genero: string): string {
    return genero === "Masculino" ? "M" : "F";
  }

  onFileSelected(event: any, tipo: "imagenSede" | "imagenQrYape") {
    const file = event.target.files[0]; // Obtiene el archivo seleccionado

    if (!file) {
      return; // Si no se selecciona ningún archivo, se detiene la ejecución
    }

    const reader = new FileReader(); // Inicializa FileReader para la vista previa

    // Callback para cuando se haya leído el archivo
    reader.onload = () => {
      if (tipo === "imagenSede") {
        this.file = file; // Guardar el archivo en la variable correspondiente
        this.fileName = file.name; // Almacenar el nombre del archivo
        this.isImageSelected = true;
        this.profileForm.get("imagenSede")?.setValue(file); // Actualizar el FormControl
        this.imagePreview = reader.result as string; // Generar la vista previa
      } else if (tipo === "imagenQrYape") {
        this.qrFile = file; // Guardar el archivo en la variable correspondiente
        this.qrFileName = file.name; // Almacenar el nombre del archivo
        this.isQrSelected = true;
        this.profileForm.get("imagenQrYape")?.setValue(file); // Actualizar el FormControl
        this.qrPreview = reader.result as string; // Generar la vista previa
      }
    };

    reader.readAsDataURL(file); // Lee el archivo seleccionado como DataURL (base64)
  }

  getUserData() {
    const id = this.authTokenUtil.getIdFromToken();
    console.log("ID: ", id);

    this.usuarioService.getUserById(id).subscribe({
      next: (data) => {
        this.userData = data;

        this.cargado = !!data.username;

        this.roleCliente = data.roles === "ROLE_CLIENTE";
        this.roleCompania = data.roles === "ROLE_COMPANIA";

        // Llamar a initForm después de obtener los datos del usuario
        this.initForm();
        console.log("User data received: ", data);
      },
      error: (error) => {
        console.log("Error:", error);
      },
    });
  }

  goBack() {
    this.router.navigate([this.rutaAnterior]);
  }

  cargarDistritos() {
    this.departamentoService.getAll().subscribe({
      next: (data) => {
        this.departamentos = data;
      },
    });
    if (this.roleCliente) {
      this.distritoService
        .getById(this.userData.cliente?.persona?.distrito!.id!)
        .subscribe({
          next: (data) => {
            console.log(data);
            this.distritoService
              .getByProvinciaId(data.provincia?.id!)
              .subscribe({
                next: (data) => {
                  this.distritos = data;
                  this.provinciaService
                    .getByDepartamentoId(
                      this.userData.cliente?.persona?.distrito?.provincia
                        ?.departamento.id!
                    )
                    .subscribe({
                      next: (data) => {
                        this.provincias = data;
                        this.ubicacionCargada = true;
                      },
                    });
                },
              });
          },
        });
    }
  }

  onDepartamentoChange(event: any) {
    this.departamentoSeleccionado = event.value;
    console.log(event.value);
    this.provinciaService.getByDepartamentoId(event.value).subscribe({
      next: (data) => {
        if (data) {
          this.provinciaHabilitado = false;
          this.provincias = data;
        }
      },
    });
  }

  onProvinciaChange(event: any) {
    this.provinciaSeleccionado = event.value;
    this.distritoService.getByProvinciaId(event.value).subscribe({
      next: (data) => {
        if (data) {
          this.distritoHabilitado = false;
          this.distritos = data;
        }
      },
    });
  }

  onDistritoChange(event: any) {
    this.distritoSeleccionado = event.value;
  }

  saveChanges() {
    if (this.profileForm.valid) {
      const id = this.authTokenUtil.getIdFromToken();
      const formData = this.profileForm.value;

      formData.genero = this.convertGeneroToBackend(formData.genero);

      const formatToHHMMSS = (hour: number): string => {
        const hours = hour < 10 ? `0${hour}` : `${hour}`;
        return `${hours}:00:00`; // Asume minutos y segundos como 00
      };

      formData.horaInicio = formatToHHMMSS(formData.horaInicio);
      formData.horaFin = formatToHHMMSS(formData.horaFin);

      console.log("FORM: ", formData.horaInicio);

      console.log("FORM: ", formData);

      if (this.roleCompania) {
        const empresa: Empresa = {
          id: this.userData.empresa?.id,
          razonSocial: formData.razonSocial,
          ruc: formData.ruc,
          telefono: formData.telefono,
          direccion: formData.direccion,
        };

        const compania: Compania = {
          id: this.userData.compania?.id,
          nombre: formData.nombre,
          concepto: formData.concepto,
          celular: formData.celular,
          correo: formData.correo,
          horaInicio: formData.horaInicio,
          horaFin: formData.horaFin,
        };

        const userDetails: UserDetails = {
          username: formData.username,
          email: formData.email,
          compania: compania,
          empresa: empresa,
        };

        if (this.isImageSelected && this.isQrSelected) {
          this.companiaService
            .updateCompaniaImage(
              this.userData.compania?.id!,
              formData.imagenSede,
              formData.imagenQrYape
            )
            .subscribe({
              next: (data) => {
                console.log("DATA: ", data);
              },
              error: (error) => {
                console.log("ERROR: ", error);
              },
            });
        }

        this.usuarioService.updateUser(id, userDetails).subscribe({
          next: (data) => {
            this.authTokenUtil.setToken(data.token);

            // Mostrar Snackbar
            const snackBarRef = this.snackBar.open(
              "DATOS ACTUALIZADOS CORRECTAMENTE",
              "Cerrar",
              {
                duration: 2000, // El snackbar se cierra automáticamente después de 2 segundos
              }
            );

            // Redirigir después de que se cierre el Snackbar
            snackBarRef.afterDismissed().subscribe(() => {
              this.router.navigate(["/home"]);
            });
          },
          error: (error) => {
            console.log("Error: ", error);
          },
        });
        return;
      }

      const distrito: Distrito = {
        id: formData.distrito,
      };

      const persona: Persona = {
        id: this.userData.cliente?.persona?.id!,
        dni: this.userData.cliente?.persona?.dni,
        nombre: this.userData.cliente?.persona?.nombre,
        apePaterno: this.userData.cliente?.persona?.apePaterno,
        apeMaterno: this.userData.cliente?.persona?.apeMaterno,
        celular: this.userData.cliente?.persona?.celular,
        correo: this.userData.cliente?.persona?.correo,
        fechaNac: this.userData.cliente?.persona?.fechaNac,
        genero: this.userData.cliente?.persona?.genero,
        direccion: formData.direccion,
        distrito: distrito,
      };

      const cliente: Cliente = {
        id: this.userData.cliente?.id,
        persona: persona,
      };

      const userDetails: UserDetails = {
        username: formData.username,
        email: formData.email,
        cliente: cliente,
      };

      console.log("DETAILS: ", userDetails);

      this.usuarioService.updateUser(id, userDetails).subscribe({
        next: (data) => {
          this.authTokenUtil.setToken(data.token);

          // Mostrar Snackbar
          const snackBarRef = this.snackBar.open(
            "DATOS ACTUALIZADOS CORRECTAMENTE",
            "Cerrar",
            {
              duration: 2000, // El snackbar se cierra automáticamente después de 2 segundos
            }
          );

          // Redirigir después de que se cierre el Snackbar
          snackBarRef.afterDismissed().subscribe(() => {
            this.router.navigate(["/home"]);
          });
        },
        error: (error) => {
          console.log("Error: ", error);
        },
      });
      return;
    }
  }

  convertTo12HourFormat(hour: number): string {
    const suffix = hour >= 12 ? "PM" : "AM";
    const hourIn12 = hour % 12 || 12; // Convertir hora a formato 12h
    return `${hourIn12}:00 ${suffix}`;
  }

  onStartForm(hora: number) {
    this.filteredEndHours = this.availableStartHours.filter(
      (hour) => hour >= hora + 8
    );

    if (
      this.profileForm.get("horaFin")?.value &&
      this.profileForm.get("horaFin")?.value < hora + 8
    ) {
      this.profileForm.get("horaFin")?.setValue(null);
    }
  }

  onStartHourChange(event: any) {
    const startHour = event.value;

    this.filteredEndHours = this.availableStartHours.filter(
      (hour) => hour >= startHour + 8
    );

    if (
      this.profileForm.get("horaFin")?.value &&
      this.profileForm.get("horaFin")?.value < startHour + 8
    ) {
      this.profileForm.get("horaFin")?.setValue(null);
    }
  }

  extractHour(timeString: string): number {
    if (!timeString) {
      throw new Error("El tiempo proporcionado no es válido.");
    }

    const [hour] = timeString.split(":"); // Divide la cadena en partes separadas por ":"
    return parseInt(hour, 10); // Convierte la parte de la hora en un número
  }
}
