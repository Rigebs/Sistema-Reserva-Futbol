import { Component, HostListener, OnInit } from "@angular/core";
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { MatInputModule } from "@angular/material/input";
import { MatStepperModule } from "@angular/material/stepper";
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { CommonModule } from "@angular/common";
import { Empresa } from "../../models/empresa";
import { Compania } from "../../models/compania";
import { Sucursal } from "../../models/sucursal";
import { Sede } from "../../models/sede";
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
import { SucursalService } from "../../services/sucursal.service";
import { SedeService } from "../../services/sede.service";
import { AuthService } from "../../services/auth.service";
import { UpdateClientSede } from "../../models/update-client-sede";
import { UsuarioService } from "../../services/usuario.service";

@Component({
  selector: "app-registrar-sede",
  standalone: true,
  imports: [
    NavbarComponent,
    MatInputModule,
    MatStepperModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatOptionModule,
    MatSelectModule,
    MatIconModule,
  ],
  templateUrl: "./registrar-sede.component.html",
  styleUrl: "./registrar-sede.component.css",
})
export class RegistrarSedeComponent implements OnInit {
  orientation: "horizontal" | "vertical" = "horizontal";
  empresaFormGroup!: FormGroup;
  companiaFormGroup!: FormGroup;
  sucursalFormGroup!: FormGroup;
  sedeFormGroup!: FormGroup;
  departamentos: Departamento[] = [];
  provincias: Provincia[] = [];
  distritos: Distrito[] = [];
  filteredProvincias: Provincia[] = [];
  filteredDistritos: Distrito[] = [];

  // Variable para almacenar el archivo de imagen seleccionado
  file: File | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private departamentoService: DepartamentoService,
    private provinciaService: ProvinciaService,
    private distritoService: DistritoService,
    private empresaService: EmpresaService,
    private companiaService: CompaniaService,
    private sucursalService: SucursalService,
    private sedeService: SedeService,
    private usuarioService: UsuarioService
  ) {}

  consultarReniec() {
    const ruc = this.empresaFormGroup.get("ruc")?.value;
    console.log("Consultando RUC en RENIEC:", ruc);
    this.empresaFormGroup.get("razonSocial")?.setValue("Empresa X SAC");
  }

  private updateOrientation() {
    this.orientation = window.innerWidth <= 600 ? "vertical" : "horizontal";
  }

  ngOnInit(): void {
    this.updateOrientation();

    this.empresaFormGroup = this.formBuilder.group({
      ruc: ["", [Validators.required, Validators.maxLength(11)]],
      razonSocial: ["", [Validators.maxLength(20)]],
      telefono: ["", [Validators.required, Validators.maxLength(11)]],
      direccion: [""],
      departamento: [null, Validators.required],
      provincia: [null, Validators.required],
      distrito: [null, Validators.required],
    });

    this.companiaFormGroup = this.formBuilder.group({
      nombre: ["", Validators.required],
      concepto: ["", Validators.required],
      correo: ["", [Validators.required, Validators.email]],
      pagWeb: [""],
      imagen: [null], // Campo de formulario para la imagen
    });

    this.sucursalFormGroup = this.formBuilder.group({
      nombre: ["", Validators.required],
    });

    this.sedeFormGroup = this.formBuilder.group({
      nombre: ["", Validators.required],
      horaInicio: ["", Validators.required],
      horaFin: ["", Validators.required],
    });

    this.cargarDepartamentos();
  }

  cargarDepartamentos() {
    this.departamentoService.getAll().subscribe((data) => {
      this.departamentos = data;
    });
  }

  onDepartamentoChange() {
    const departamentoId = this.empresaFormGroup.get("departamento")?.value;
    this.provinciaService
      .getByDepartamentoId(departamentoId)
      .subscribe((data) => {
        this.filteredProvincias = data;
        this.filteredDistritos = [];
        this.empresaFormGroup.get("provincia")?.setValue(null);
        this.empresaFormGroup.get("distrito")?.setValue(null);
      });
  }

  onProvinciaChange() {
    const provinciaId = this.empresaFormGroup.get("provincia")?.value;
    this.distritoService.getByProvinciaId(provinciaId).subscribe((data) => {
      this.filteredDistritos = data;
      this.empresaFormGroup.get("distrito")?.setValue(null);
    });
  }

  // Método para manejar la selección de la imagen
  onFileSelected(event: any) {
    this.file = event.target.files[0]; // Almacenar el archivo en la variable `file`
    console.log("Imagen seleccionada:", this.file);
  }

  submitForm() {
    const empresaFormValue = this.empresaFormGroup.value;
    const distritoId = empresaFormValue.distrito;
    const distrito: Distrito = { id: distritoId };

    const empresa: Empresa = {
      ruc: empresaFormValue.ruc,
      razonSocial: empresaFormValue.razonSocial,
      telefono: empresaFormValue.telefono,
      direccion: empresaFormValue.direccion,
      distrito: distrito,
    };

    // Crear la empresa y obtener su id para asignarla a compania
    this.empresaService.createEmpresa(empresa).subscribe((empresaData) => {
      console.log("Empresa creada:", empresaData);

      // Usamos solo el ID de la empresa para crear la compañía
      const compania: Compania = {
        nombre: this.companiaFormGroup.get("nombre")?.value,
        concepto: this.companiaFormGroup.get("concepto")?.value,
        correo: this.companiaFormGroup.get("correo")?.value,
        pagWeb: this.companiaFormGroup.get("pagWeb")?.value,
        empresa: { id: empresaData.id }, // Solo el ID de la empresa
      };

      console.log("compania: ", compania);

      // Asegurarse de que 'file' no sea null al momento de enviar
      this.companiaService
        .saveCompania(this.file!, compania)
        .subscribe((companiaData) => {
          console.log("Compañía creada con imagen:", companiaData);

          // Usamos solo el ID de la compañía para crear la sucursal
          const sucursal: Sucursal = {
            nombre: this.sucursalFormGroup.get("nombre")?.value,
            compania: { id: companiaData.id }, // Solo el ID de la compañía
          };

          // Crear sucursal y obtener su id para asignarla a sede
          this.sucursalService
            .createSucursal(sucursal)
            .subscribe((sucursalData) => {
              console.log("Sucursal creada:", sucursalData);

              // Formatear la horaInicio y horaFin a 'hh:mm:ss'
              let horaInicio = this.sedeFormGroup.get("horaInicio")?.value;
              let horaFin = this.sedeFormGroup.get("horaFin")?.value;

              if (horaInicio && horaInicio.length === 5) {
                horaInicio += ":00"; // Agregar segundos si solo tiene hh:mm
              }

              if (horaFin && horaFin.length === 5) {
                horaFin += ":00"; // Agregar segundos si solo tiene hh:mm
              }

              // Usamos solo el ID de la sucursal para crear la sede
              const sede: Sede = {
                nombre: this.sedeFormGroup.get("nombre")?.value,
                horaInicio: horaInicio,
                horaFin: horaFin,
                sucursal: { id: sucursalData.id }, // Solo el ID de la sucursal
              };

              console.log("SEDE: ", sede);

              // Crear sede
              this.sedeService.createSede(sede).subscribe((sedeData) => {
                console.log("Sede creada:", sedeData);

                // Llamar al servicio authService para actualizar el cliente y la sede
                const updateRequest: UpdateClientSede = {
                  clienteId: null, // Ya que no se ha especificado un cliente, lo dejamos como null
                  sedeId: sedeData.id, // Usamos el ID de la sede recién creada
                };

                // Actualizamos la relación cliente y sede
                this.usuarioService.updateClientOrSede(updateRequest).subscribe(
                  (response) => {
                    console.log("Relación cliente-sede actualizada:", response);
                  },
                  (error) => {
                    console.error(
                      "Error al actualizar relación cliente-sede:",
                      error
                    );
                  }
                );
              });
            });
        });
    });
  }
}
