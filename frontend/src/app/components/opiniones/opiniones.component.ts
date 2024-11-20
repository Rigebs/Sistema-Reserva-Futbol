import { Component, Input, OnInit } from "@angular/core";
import { OpinionService } from "../../services/opinion.service";
import { Opinion } from "../../models/opinion";
import { CommonModule } from "@angular/common";
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { OpinionSummary } from "../../models/opinion-summary";

@Component({
  selector: "app-opiniones",
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: "./opiniones.component.html",
  styleUrl: "./opiniones.component.css",
})
export class OpinionesComponent implements OnInit {
  @Input() companiaId!: number; // Recibe el ID de la compañía desde el componente padre

  opinionSummary!: OpinionSummary | null;

  opiniones: Opinion[] = [];
  opinionForm!: FormGroup;

  constructor(
    private opinionService: OpinionService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    // Inicializa el formulario de opinión
    this.opinionForm = this.fb.group({
      contenido: ["", [Validators.required, Validators.maxLength(500)]],
      calificacion: [
        5,
        [Validators.required, Validators.min(1), Validators.max(5)],
      ],
    });
    this.loadOpinionSummary();

    // Obtener las opiniones si el companiaId está disponible
    if (this.companiaId) {
      this.obtenerOpiniones();
    }
  }

  loadOpinionSummary(): void {
    this.opinionService.getOpinionSummary(this.companiaId).subscribe({
      next: (data) => {
        this.opinionSummary = data;
      },
      error: (err) => {
        console.error("Error al cargar los datos de opiniones:", err);
        this.opinionSummary = null;
      },
    });
  }

  calculatePercentage(count: number): number {
    if (!this.opinionSummary || this.opinionSummary.totalReviews === 0) {
      return 0;
    }
    return (count / this.opinionSummary.totalReviews) * 100;
  }

  getStarCount(star: number): number {
    if (!this.opinionSummary) {
      return 0;
    }

    switch (star) {
      case 1:
        return this.opinionSummary.star1Count;
      case 2:
        return this.opinionSummary.star2Count;
      case 3:
        return this.opinionSummary.star3Count;
      case 4:
        return this.opinionSummary.star4Count;
      case 5:
        return this.opinionSummary.star5Count;
      default:
        return 0;
    }
  }

  obtenerOpiniones(): void {
    this.opinionService
      .obtenerOpinionesPorCompania(this.companiaId)
      .subscribe((opiniones) => {
        console.log("OPINIIOPNES: ", opiniones);

        this.opiniones = opiniones;
      });
  }

  getEstrellasPromedio(): string[] {
    if (!this.opinionSummary || this.opinionSummary.totalReviews === 0) {
      return []; // No hay opiniones, no mostrar estrellas
    }

    const estrellas: string[] = [];
    const promedio = this.opinionSummary.averageRating;

    for (let i = 1; i <= 5; i++) {
      if (i <= Math.floor(promedio)) {
        estrellas.push("completa"); // Estrella llena
      } else if (i - promedio < 1) {
        estrellas.push("mitad"); // Estrella a la mitad
      } else {
        estrellas.push("vacía"); // Estrella vacía
      }
    }

    return estrellas;
  }

  enviarOpinion(): void {
    if (this.opinionForm.valid && this.companiaId) {
      const newOpinion: Opinion = {
        contenido: this.opinionForm.value.contenido,
        calificacion: this.opinionForm.value.calificacion,
        companiaId: this.companiaId,
      };

      console.log("OPINION A ENVIAR: ", newOpinion);

      this.opinionService.agregarOpinion(newOpinion).subscribe((opinion) => {
        this.opiniones.push(opinion); // Agregar la nueva opinión
        this.opinionForm.reset(); // Limpiar el formulario
        this.loadOpinionSummary();
      });
    }
  }

  seleccionarCalificacion(calificacion: number): void {
    this.opinionForm.patchValue({ calificacion });
  }

  // Obtener la cantidad de estrellas para visualización
  getEstrellas(calificacion: number): number[] {
    return new Array(calificacion).fill(0);
  }
}
