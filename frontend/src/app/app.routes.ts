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
import { SupremAdminComponent } from "./pages/suprem-admin/suprem-admin.component";
import { SupremAdminGuard } from "./guards/suprem-admin.guard";
import { EditarPerfilComponent } from "./pages/editar-perfil/editar-perfil.component";
import { DashboardComponent } from "./pages/dashboard/dashboard.component";
import { ComprobanteComponent } from "./pages/comprobante/comprobante.component";

export const routes: Routes = [
  { path: "", redirectTo: "/home", pathMatch: "full" },
  { path: "home", component: HomeComponent },
  { path: "pasarela-pago", component: PasarelaPagoComponent },
  {
    path: ":userId/reservar-campo/compania/:companiaId",
    component: ReservarCampoComponent,
  },
  {
    path: "confir",
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
  {
    path: ":usuario/panel-suprem",
    component: SupremAdminComponent,
    canActivate: [SupremAdminGuard],
  },
  {
    path: "usuario/editar-perfil",
    component: EditarPerfilComponent,
  },
  {
    path: ":usuario/panel-admin/dashboard",
    component: DashboardComponent,
    canActivate: [AdminGuard],
  },
  {
    path: "comprobante",
    component: ComprobanteComponent,
  },

  {
    path: "registrar-cliente",
    component: RegistrarClienteComponent,
    canActivate: [AuthGuard],
  },
  { path: "confirm", component: ConfirmarComponent },
];
