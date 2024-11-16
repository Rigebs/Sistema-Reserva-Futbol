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

export const routes: Routes = [
  { path: "", redirectTo: "/home", pathMatch: "full" },
  { path: "home", component: HomeComponent },
  { path: "pasarela-pago", component: PasarelaPagoComponent },
  { path: ":userId/reservar-campo", component: ReservarCampoComponent },
  {
    path: ":usuario/panel-admin",
    component: PanelAdminComponent,
    canActivate: [AuthGuard, AdminGuard],
  },
  { path: "panel-admin/campos", component: GestionarCamposComponent },
  { path: "panel-admin/reservaciones", component: ReservacionesComponent },
  {
    path: "registrar-sede",
    component: RegistrarSedeComponent,
    canActivate: [AuthGuard],
  },
  { path: "registrar-cliente", component: RegistrarClienteComponent },
];
