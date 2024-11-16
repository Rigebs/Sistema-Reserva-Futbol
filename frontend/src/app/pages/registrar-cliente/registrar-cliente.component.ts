import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import {
  FormBuilder,
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
import { Persona } from "../../models/persona";
import { Empresa } from "../../models/empresa";
import { SeleccionarUbicacionComponent } from "../../components/seleccionar-ubicacion/seleccionar-ubicacion.component";
import { PersonaService } from "../../services/persona.service";
import { EmpresaService } from "../../services/empresa.service";
import { UsuarioService } from "../../services/usuario.service";

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
  ],
  templateUrl: "./registrar-cliente.component.html",
  styleUrl: "./registrar-cliente.component.css",
})
export class RegistrarClienteComponent {
  selectedType: "persona" | "empresa" | null = null;

  personaForm: FormGroup;
  empresaForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private personaService: PersonaService,
    private usuarioService: UsuarioService,
    private empresaService: EmpresaService
  ) {
    this.personaForm = this.fb.group({
      dni: [
        "",
        [
          Validators.required,
          Validators.pattern(/^\d{8}$/), // Valida que sean exactamente 8 números
        ],
      ],
      nombre: ["", Validators.required],
      apePaterno: ["", Validators.required],
      apeMaterno: ["", Validators.required],
      celular: ["", Validators.required],
      correo: ["", [Validators.required, Validators.email]],
      fechaNac: ["", Validators.required],
      genero: ["", Validators.required],
      direccion: ["", Validators.required],
      distrito: [null, Validators.required],
    });

    this.empresaForm = this.fb.group({
      ruc: [
        "",
        [
          Validators.required,
          Validators.pattern(/^\d{11}$/), // Valida que sean exactamente 11 números
        ],
      ],
      razonSocial: ["", Validators.required],
      telefono: ["", Validators.required],
      direccion: ["", Validators.required],
      distrito: [null, Validators.required],
    });

    this.setupDniWatcher();
    this.setupRucWatcher();
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
    this.usuarioService.updateClientOrSede(updateRequest).subscribe(() => {
      console.log("Cliente vinculado con éxito");
    });
  }

  setupDniWatcher(): void {
    this.personaForm.get("dni")?.valueChanges.subscribe((dni) => {
      if (dni.length === 8 && /^\d{8}$/.test(dni)) {
        this.personaService.consultarDni(dni).subscribe((data) => {
          console.log("Datos del DNI:", data);
          this.autocompletarPersona(data);
        });
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

  autocompletarEmpresa(data: any): void {
    this.empresaForm.patchValue({
      razonSocial: data.razonSocial,
      telefono: data.telefono || "", // Si el servicio devuelve un teléfono, lo autocompletamos
      direccion: data.direccion || "", // Si el servicio devuelve una dirección, la autocompletamos
    });
  }

  registrar(): void {
    if (this.selectedType === "persona" && this.personaForm.valid) {
      const personaData = this.personaForm.value;
      this.personaService.crearPersona(personaData).subscribe((data) => {
        this.vincularCliente(data.clienteId);
        console.log("Persona registrada:", data);
      });
    } else if (this.selectedType === "empresa" && this.empresaForm.valid) {
      const empresaData = this.empresaForm.value;
      this.empresaService.createEmpresa(empresaData).subscribe((data) => {
        this.vincularCliente(data.clienteId);
        console.log("Empresa registrada:", data);
      });
    }
  }
}
