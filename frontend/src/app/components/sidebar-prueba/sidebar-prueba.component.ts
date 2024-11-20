import { CommonModule } from "@angular/common";
import { Component, HostListener, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { AuthTokenUtil } from "../../utils/auth-token-util";
import { Router, RouterModule } from "@angular/router";

@Component({
  selector: "app-sidebar-prueba",
  standalone: true,
  imports: [MatIconModule, CommonModule, MatButtonModule, RouterModule],
  templateUrl: "./sidebar-prueba.component.html",
  styleUrl: "./sidebar-prueba.component.css",
})
export class SidebarPruebaComponent implements OnInit {
  isMobile: boolean = false; // Detecta si es pantalla móvil
  isSidebarOpen: boolean = false; // Controla si el sidebar está abierto (en móvil)
  usuario: string | undefined; // Aquí deberías obtener el usuario dinámicamente

  constructor(private authTokenUtil: AuthTokenUtil) {}

  ngOnInit() {
    this.checkScreenSize();
    this.getUsuario();
  }

  getUsuario() {
    this.usuario = this.authTokenUtil.getUserFromToken();
  }

  @HostListener("window:resize", [])
  checkScreenSize() {
    this.isMobile = window.innerWidth < 768;
  }

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }
}
