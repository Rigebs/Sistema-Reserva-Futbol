import { Routes } from "@angular/router";
import { LoginComponent } from "./pages/login/login.component";
import { HomeComponent } from "./pages/home/home.component";
import { ReservarCampoComponent } from "./pages/reservar-campo/reservar-campo.component";
import { PasarelaPagoComponent } from "./pages/pasarela-pago/pasarela-pago.component";
import { PanelAdminComponent } from "./pages/panel-admin/panel-admin.component";
import { GestionarCamposComponent } from "./admin/gestionar-campos/gestionar-campos.component";
import { ReservacionesComponent } from "./admin/reservaciones/reservaciones.component";

export const routes: Routes = [
  { path: "login", component: LoginComponent },
  { path: "", redirectTo: "/home", pathMatch: "full" },
  { path: "home", component: HomeComponent },
  { path: "pasarela-pago", component: PasarelaPagoComponent },
  { path: "reservar-campo", component: ReservarCampoComponent },
  { path: "panel-admin", component: PanelAdminComponent },
  { path: "panel-admin/campos", component: GestionarCamposComponent },
  { path: "panel-admin/reservaciones", component: ReservacionesComponent },
];
