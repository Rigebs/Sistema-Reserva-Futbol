import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { GoogleApiService } from "./services/google-api.service";

@Component({
  selector: "app-root",
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: "./app.component.html",
  styleUrl: "./app.component.css",
  providers: [],
})
export class AppComponent {}
