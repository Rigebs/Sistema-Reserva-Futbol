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
import { LoginComponent } from "../../auth/login/login.component";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MatDialog } from "@angular/material/dialog";

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
  companiaId!: number;
  sedeConCampos: SedeWithCampo | undefined;
  campos: Campo[] = [];
  isLoading = true;

  constructor(
    private router: Router,
    private campoService: CampoService,
    private route: ActivatedRoute,
    private authTokenUtil: AuthTokenUtil,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.userId = +params.get("userId")!;
      this.companiaId = +params.get("companiaId")!;
      this.obtenerCampos(this.userId);
    });
  }

  obtenerCampos(userId: number): void {
    this.isLoading = true;

    this.campoService.getCamposByUsuarioId(userId).subscribe(
      (response) => {
        this.sedeConCampos = Array.isArray(response) ? response[0] : response;
        console.log("f;:", this.sedeConCampos);

        this.campos = this.sedeConCampos?.camposWithSede || [];
        this.isLoading = false;
      },
      (error) => {
        console.error("Error al obtener los campos:", error);
        this.isLoading = false;
      }
    );
  }

  agregarReservaFinalizada(reserva: any) {
    this.reservasFinalizadas.push(reserva);
  }

  irPasarelaDePago(): void {
    if (this.isReservaFinalizadaEmpty) {
      this.snackBar.open("Debes de seleccionar un campo", "Cerrar", {
        duration: 3000,
        horizontalPosition: "center",
        verticalPosition: "top",
      });
    } else {
      const tokenValido = this.authTokenUtil.isTokenValid();
      const tokenDecoded = tokenValido
        ? this.authTokenUtil.decodeToken()
        : null;
      const roles = tokenDecoded?.roles || [];
      const esCliente = roles.includes("ROLE_CLIENTE");
      const esEspera = roles.includes("ROLE_ESPERA");
      const esCompania = roles.includes("ROLE_COMPANIA");

      if (!tokenValido) {
        const isMobile = window.innerWidth <= 768;
        const dialogConfig = {
          minWidth: isMobile ? "" : "900px",
          minHeight: isMobile ? "" : "500px",
        };

        this.dialog.open(LoginComponent, dialogConfig);
        this.snackBar.open("Primero debes iniciar sesión", "Cerrar", {
          duration: 3000,
          horizontalPosition: "center",
          verticalPosition: "top",
        });
      } else if (esCompania || esEspera) {
        this.snackBar.open(
          "Los usuarios con rol de compañía o en espera no pueden reservar campos",
          "Cerrar",
          {
            duration: 3000,
            horizontalPosition: "center",
            verticalPosition: "top",
          }
        );
      } else if (!esCliente) {
        this.snackBar.open("Primero debes registrarte como cliente", "Cerrar", {
          duration: 3000,
          horizontalPosition: "center",
          verticalPosition: "top",
        });

        this.router.navigate(["/registrar-cliente"]);
      } else {
        this.router.navigate(["/pasarela-pago"], {
          state: { reservas: this.reservasFinalizadas },
        });
      }
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

  get isReservaFinalizadaEmpty(): boolean {
    return this.reservasFinalizadas.length === 0;
  }
}
