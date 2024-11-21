import { Routes } from "@angular/router";
import { HomeComponent } from "./pages/home/home.component";
import { ReservarCampoComponent } from "./pages/reservar-campo/reservar-campo.component";
import { PasarelaPagoComponent } from "./pages/pasarela-pago/pasarela-pago.component";
import { PanelAdminComponent } from "./pages/panel-admin/panel-admin.component";
import { GestionarCamposComponent } from "./admin/gestionar-campos/gestionar-campos.component";
import { ReservacionesComponent } from "./admin/reservaciones/reservaciones.component";
import { RegistrarSedeComponent } from "./pages/registrar-sede/registrar-sede.component";
import { AuthGuard } from "./auth.guard";
import { AdminGuard } from "./guards/admin.guard";
import { RegistrarClienteComponent } from "./pages/registrar-cliente/registrar-cliente.component";
import { ConfirmarComponent } from "./pages/confirmar/confirmar.component";
import { GoogleConfirmationComponent } from "./pages/google-confirmation/google-confirmation.component";

export const routes: Routes = [
  { path: "", redirectTo: "/home", pathMatch: "full" },
  { path: "home", component: HomeComponent },
  { path: "pasarela-pago", component: PasarelaPagoComponent },
  {
    path: ":userId/reservar-campo/compania/:companiaId",
    component: ReservarCampoComponent,
  },
  {
    path: "login/oauth2/code/google",
    component: GoogleConfirmationComponent,
  },
  {
    path: ":usuario/panel-admin",
    component: PanelAdminComponent,
    canActivate: [AuthGuard, AdminGuard],
  },
  {
    path: ":usuario/panel-admin/campos",
    component: GestionarCamposComponent,
    canActivate: [AdminGuard],
  },
  {
    path: ":usuario/panel-admin/reservaciones",
    component: ReservacionesComponent,
    canActivate: [AdminGuard],
  },
  {
    path: "registrar-sede",
    component: RegistrarSedeComponent,
    canActivate: [AuthGuard],
  },

  { path: "registrar-cliente", component: RegistrarClienteComponent },
  { path: "confirm", component: ConfirmarComponent },
];
