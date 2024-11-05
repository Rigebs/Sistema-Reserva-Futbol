import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { MatIconModule } from "@angular/material/icon";
import { MatListItem, MatNavList } from "@angular/material/list";
import { MatSidenavModule } from "@angular/material/sidenav";
import { MatToolbarModule } from "@angular/material/toolbar";
import { RouterLink } from "@angular/router";

@Component({
  selector: "app-sidebar",
  standalone: true,
  imports: [
    MatSidenavModule,
    MatToolbarModule,
    MatIconModule,
    MatListItem,
    RouterLink,
    CommonModule,
    MatNavList,
  ],
  templateUrl: "./sidebar.component.html",
  styleUrl: "./sidebar.component.css",
})
export class SidebarComponent {
  isCollapsed: boolean = true;

  // Opciones de menú
  menuItems = [
    { icon: "home", title: "Inicio", route: "/home" },
    { icon: "sports_soccer", title: "Campos", route: "/panel-admin/campos" },
    { icon: "event", title: "Reservas", route: "/panel-admin/reservaciones" },
    { icon: "settings", title: "Configuración", route: "/settings" },
  ];

  // Función para colapsar el menú
  toggleCollapse() {
    this.isCollapsed = !this.isCollapsed;
  }
}
