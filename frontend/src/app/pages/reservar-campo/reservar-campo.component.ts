import { Component, OnInit, ViewChild } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { ProcesoReservaComponent } from "../../components/proceso-reserva/proceso-reserva.component";
import { CommonModule } from '@angular/common';
import { MatNativeDateModule } from '@angular/material/core';
import { ReservaListComponent } from "../../components/reserva-list/reserva-list.component";
import { MatCardModule } from '@angular/material/card';
import { CamposResumenComponent } from "../../components/campos-resumen/campos-resumen.component";
import { ActivatedRoute, Router } from '@angular/router';
import { CampoService } from '../../services/campo.service';

@Component({
  selector: 'app-reservar-campo',
  standalone: true,
  imports: [NavbarComponent, ProcesoReservaComponent,
    MatCardModule,
    CommonModule, MatNativeDateModule, ReservaListComponent, CamposResumenComponent],
  templateUrl: './reservar-campo.component.html',
  styleUrl: './reservar-campo.component.css'
})
export class ReservarCampoComponent implements OnInit {
  @ViewChild('reservaList') reservaListComponent: any;

  reservas: any[] = [];
  reservasFinalizadas: any[] = [];
  userId!: number;

  constructor(
    private router: Router,
    private campoService: CampoService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Obtener el userId de la URL
    this.route.paramMap.subscribe((params) => {
      this.userId = +params.get('userId')!;
      this.obtenerCampos(this.userId);
    });
  }

  // Método para obtener los campos desde la API
  obtenerCampos(userId: number): void {
    this.campoService.getCamposByUsuarioId(userId).subscribe(
      (campos) => {
        this.reservas = campos;
      },
      (error) => {
        console.error('Error al obtener los campos', error);
      }
    );
  }

  // Método para agregar una reserva finalizada
  agregarReservaFinalizada(reserva: any) {
    this.reservasFinalizadas.push(reserva);
  }

  // Método para navegar a la pasarela de pago
  irPasarelaDePago() {
    this.router.navigate(['/pasarela-pago'], { state: { reservas: this.reservasFinalizadas } });
  }

  onReservaEliminada(reserva: any) {
    this.reservasFinalizadas = this.reservasFinalizadas.filter((r) => r !== reserva);
    this.reservaListComponent.hola(reserva);
  }
}