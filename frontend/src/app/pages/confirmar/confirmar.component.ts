import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { AuthTokenUtil } from "../../utils/auth-token-util";

@Component({
  selector: "app-confirmar",
  standalone: true,
  imports: [],
  templateUrl: "./confirmar.component.html",
  styleUrl: "./confirmar.component.css",
})
export class ConfirmarComponent implements OnInit {
  token: string | null = null;
  usuario: string | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authTokenUtil: AuthTokenUtil
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get("token");

    this.usuario = this.authTokenUtil.getUserFromToken();

    if (this.token) {
      console.log("Token recibido:", this.token);
      this.authTokenUtil.setToken(this.token);
    }

    setTimeout(() => {
      this.router.navigate([`/${this.usuario}/panel-admin`]);
      console.log("ruta: ", `/${this.usuario}/panel-admin`);
    }, 2000);
  }
}
