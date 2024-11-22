import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";

@Component({
  selector: "app-google-confirmation",
  standalone: true,
  imports: [],
  templateUrl: "./google-confirmation.component.html",
  styleUrl: "./google-confirmation.component.css",
})
export class GoogleConfirmationComponent {
  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const token = params["token"];
      if (token) {
        localStorage.setItem("authToken", token);
        this.router.navigate(["/home"]);
      } else {
        console.error("Token no encontrado en la URL");
        this.router.navigate(["/home"]);
      }
    });
  }
}
