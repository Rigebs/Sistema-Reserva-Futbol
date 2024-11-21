import { Component } from "@angular/core";
import { GoogleApiService } from "../../services/google-api.service";

@Component({
  selector: "app-google-confirmation",
  standalone: true,
  imports: [],
  templateUrl: "./google-confirmation.component.html",
  styleUrl: "./google-confirmation.component.css",
})
export class GoogleConfirmationComponent {
  constructor(private googleApiService: GoogleApiService) {}
}
