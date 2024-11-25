import { Component } from "@angular/core";
import { CompaniaService } from "../../services/compania.service";
import { CommonModule } from "@angular/common";
import { EmpresaCompania } from "../../models/empresa-compania";
import { CompaniaCardComponent } from "../../components/compania-card/compania-card.component";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatTableModule } from "@angular/material/table";

@Component({
  selector: "app-suprem-admin",
  standalone: true,
  imports: [
    CommonModule,
    CompaniaCardComponent,
    MatTableModule,
    MatSnackBarModule,
  ],
  templateUrl: "./suprem-admin.component.html",
  styleUrl: "./suprem-admin.component.css",
})
export class SupremAdminComponent {
  empresasCompanias: EmpresaCompania[] = [];
  companias: any[] = [];
  mostrarTabla: boolean = false;
  mostrarCards: boolean = false;

  selectedCard: string = "";

  displayedColumns: string[] = ["id", "nombre", "correo", "celular"];

  selectCard(cardName: string) {
    this.selectedCard = cardName;
  }

  constructor(private companiaService: CompaniaService) {}

  ngOnInit(): void {}

  onListarCompanias(): void {
    this.companiaService.getAllCompanias().subscribe({
      next: (data) => {
        this.companias = data;
        this.mostrarTabla = true;
        this.mostrarCards = false;
      },
      error: (err) => console.error("Error al listar compañías:", err),
    });
  }

  onGestionarPeticiones(): void {
    this.companiaService.getCompaniasEnEspera().subscribe({
      next: (data) => {
        console.log("da: ", data);

        this.empresasCompanias = data;
        this.mostrarTabla = false;
        this.mostrarCards = true;
      },
      error: (err) => console.error("Error al gestionar peticiones:", err),
    });
  }

  eliminarCompania(idCompania: number): void {
    const companiaIndex = this.empresasCompanias.findIndex(
      (compania) => compania.idCompania === idCompania
    );

    if (companiaIndex !== -1) {
      this.empresasCompanias.splice(companiaIndex, 1);
      console.log(`Compañía con ID ${idCompania} eliminada del listado.`);
    } else {
      console.warn(`No se encontró una compañía con ID ${idCompania}.`);
    }
  }
}
