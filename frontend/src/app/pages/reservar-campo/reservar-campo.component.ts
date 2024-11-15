import { Component, OnInit, ViewChild } from "@angular/core";
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { CommonModule } from "@angular/common";
import { MatNativeDateModule } from "@angular/material/core";
import { ReservaListComponent } from "../../components/reserva-list/reserva-list.component";
import { MatCardModule } from "@angular/material/card";
import { CamposResumenComponent } from "../../components/campos-resumen/campos-resumen.component";
import { ActivatedRoute, Router } from "@angular/router";
import { CampoService } from "../../services/campo.service";
import { SedeWithCampo } from "../../models/sede-with-campo";
import { Campo } from "../../models/campo";
import { LoadingComponent } from "../../components/loading/loading.component";
import { MatIconModule } from "@angular/material/icon";
import { MatDividerModule } from "@angular/material/divider";
import { OpinionesComponent } from "../../components/opiniones/opiniones.component";
import { AuthTokenUtil } from "../../utils/auth-token-util";

@Component({
  selector: "app-reservar-campo",
  standalone: true,
  imports: [
    NavbarComponent,
    MatCardModule,
    CommonModule,
    MatNativeDateModule,
    ReservaListComponent,
    CamposResumenComponent,
    LoadingComponent,
    MatIconModule,
    MatDividerModule,
    OpinionesComponent,
  ],
  templateUrl: "./reservar-campo.component.html",
  styleUrl: "./reservar-campo.component.css",
})
export class ReservarCampoComponent implements OnInit {
  @ViewChild("reservaList") reservaListComponent: any;

  reservasFinalizadas: any[] = [];
  userId!: number;
  sedeConCampos: SedeWithCampo | undefined;
  campos: Campo[] = [];
  isLoading = true; // Variable para controlar la pantalla de carga

  constructor(
    private router: Router,
    private campoService: CampoService,
    private route: ActivatedRoute,
    private authTokenUtil: AuthTokenUtil
  ) {}

  ngOnInit(): void {
    // Obtener el userId de la URL
    this.route.paramMap.subscribe((params) => {
      this.userId = +params.get("userId")!;
      this.obtenerCampos(this.userId);
    });
  }

  // Método para obtener los campos desde la API
  obtenerCampos(userId: number): void {
    this.isLoading = true; // Activamos la pantalla de carga al iniciar la petición

    this.campoService.getCamposByUsuarioId(userId).subscribe(
      (response) => {
        this.sedeConCampos = Array.isArray(response) ? response[0] : response;
        this.campos = this.sedeConCampos?.camposWithSede || [];
        this.isLoading = false; // Desactivamos la pantalla de carga cuando los datos llegan
        console.log("DA: ", response);
      },
      (error) => {
        console.error("Error al obtener los campos:", error);
        this.isLoading = false; // Desactivamos la pantalla de carga incluso si ocurre un error
      }
    );
  }

  // Método para agregar una reserva finalizada
  agregarReservaFinalizada(reserva: any) {
    this.reservasFinalizadas.push(reserva);
    console.log("INDEFINIDO: ", reserva);
  }

  // Método para navegar a la pasarela de pago
  irPasarelaDePago(): void {
    // Verificar si el token contiene el rol ROLE_CLIENTE
    const tokenValido = this.authTokenUtil.isTokenValid();
    const esCliente =
      tokenValido &&
      this.authTokenUtil.decodeToken()?.roles?.includes("ROLE_CLIENTE");

    if (esCliente) {
      // Redirigir a pasarela-pago con estado
      this.router.navigate(["/pasarela-pago"], {
        state: { reservas: this.reservasFinalizadas },
      });
    } else {
      // Redirigir al formulario de registro
      this.router.navigate(["/registro"]);
    }
  }

  onReservaEliminada(reserva: any) {
    this.reservasFinalizadas = this.reservasFinalizadas.filter(
      (r) => r !== reserva
    );
    if (this.reservaListComponent) {
      this.reservaListComponent.hola(reserva);
    }
  }
}
