import { Component, OnInit } from "@angular/core";
import { SidebarPruebaComponent } from "../../components/sidebar-prueba/sidebar-prueba.component";
import { AuthTokenUtil } from "../../utils/auth-token-util";

@Component({
  selector: "app-panel-admin",
  standalone: true,
  imports: [SidebarPruebaComponent],
  templateUrl: "./panel-admin.component.html",
  styleUrl: "./panel-admin.component.css",
})
export class PanelAdminComponent implements OnInit {
  usuario: string | undefined;

  constructor(private authTokenUtil: AuthTokenUtil) {}
  ngOnInit(): void {
    this.getUsuario();
  }

  getUsuario() {
    this.usuario = this.authTokenUtil.getUserFromToken();
  }
}
