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
import { UpdateClientCompania } from "../../models/update-client-sede";
import { UsuarioService } from "../../services/usuario.service";

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
  ],
  templateUrl: "./registrar-sede.component.html",
  styleUrl: "./registrar-sede.component.css",
})
export class RegistrarSedeComponent implements OnInit {
  orientation: "horizontal" | "vertical" = "horizontal";
  empresaFormGroup!: FormGroup;
  companiaFormGroup!: FormGroup;
  departamentos: Departamento[] = [];
  provincias: Provincia[] = [];
  distritos: Distrito[] = [];
  filteredProvincias: Provincia[] = [];
  filteredDistritos: Distrito[] = [];

  // Variable para almacenar el archivo de imagen seleccionado
  file: File | null = null;
  qrFile: File | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private departamentoService: DepartamentoService,
    private provinciaService: ProvinciaService,
    private distritoService: DistritoService,
    private empresaService: EmpresaService,
    private companiaService: CompaniaService,
    private usuarioService: UsuarioService
  ) {}

  consultarReniec() {
    const ruc = this.empresaFormGroup.get("ruc")?.value;

    // Llamamos al servicio consultarRuc pasando el RUC
    this.empresaService.consultarRuc(ruc).subscribe(
      (response) => {
        // Comprobamos si la respuesta tiene la propiedad razonSocial
        if (response && response.razonSocial) {
          // Actualizamos el campo razonSocial en el formulario con el valor recibido
          this.empresaFormGroup
            .get("razonSocial")
            ?.setValue(response.razonSocial);

          this.empresaFormGroup.get("direccion")?.setValue(response.direccion);
        } else {
          console.error(
            "Razon social no encontrada en la respuesta:",
            response
          );
        }
      },
      (error) => {
        console.error("Error al consultar RUC:", error);
      }
    );
  }

  private updateOrientation() {
    this.orientation = window.innerWidth <= 600 ? "vertical" : "horizontal";
  }

  ngOnInit(): void {
    this.updateOrientation();

    // Formulario de Empresa
    this.empresaFormGroup = this.formBuilder.group({
      ruc: ["", [Validators.required, Validators.maxLength(11)]],
      razonSocial: ["", [Validators.maxLength(100)]], // Cambiar el valor de maxLength si es necesario
      telefono: ["", [Validators.required, Validators.maxLength(11)]],
      direccion: [""],
      departamento: [null, Validators.required],
      provincia: [null, Validators.required],
      distrito: [null, Validators.required],
    });

    // Formulario de Compañía
    this.companiaFormGroup = this.formBuilder.group({
      nombre: ["", Validators.required],
      concepto: ["", Validators.required],
      correo: ["", [Validators.required, Validators.email]],
      telefono: ["", [Validators.required, Validators.maxLength(11)]],
      pagWeb: [""],
      imagenSede: [null], // Campo de imagen de sede
      imagenQrYape: [null], // Campo de imagen QR de Yape
      horaInicio: ["", Validators.required],
      horaFin: ["", Validators.required],
    });

    // Cargar departamentos
    this.cargarDepartamentos();
  }

  // Método para manejar la selección de archivos
  onFileSelected(event: any, tipo: "imagenSede" | "imagenQrYape") {
    const file = event.target.files[0];

    if (tipo === "imagenSede") {
      this.file = file; // Guardar en la variable `file`
      this.companiaFormGroup.get("imagenSede")?.setValue(file);
    } else if (tipo === "imagenQrYape") {
      this.qrFile = file; // Guardar en la variable `qrFile`
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

    this.empresaService.createEmpresa(empresa).subscribe((empresaData) => {
      console.log("Empresa creada:", empresaData);

      let horaInicio = this.companiaFormGroup.get("horaInicio")?.value;
      let horaFin = this.companiaFormGroup.get("horaFin")?.value;

      if (horaInicio && horaInicio.length === 5) {
        horaInicio += ":00";
      }

      if (horaFin && horaFin.length === 5) {
        horaFin += ":00";
      }

      const compania: Compania = {
        nombre: this.companiaFormGroup.get("nombre")?.value,
        concepto: this.companiaFormGroup.get("concepto")?.value,
        correo: this.companiaFormGroup.get("correo")?.value,
        celular: this.companiaFormGroup.get("telefono")?.value,
        horaInicio: horaInicio,
        horaFin: horaFin,
        empresa: { id: empresaData.id },
      };

      this.companiaService
        .saveCompania(this.qrFile!, this.file!, compania)
        .subscribe((companiaData) => {
          const updateRequest: UpdateClientCompania = {
            clienteId: null,
            companiaId: companiaData.id,
          };

          this.usuarioService
            .updateClientOrSede(updateRequest)
            .subscribe((response) => {
              console.log("Relación cliente-sede actualizada:", response);
            });
        });
    });
  }
}
