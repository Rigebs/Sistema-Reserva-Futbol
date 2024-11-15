import { Component, Input, OnInit } from "@angular/core";
import { OpinionService } from "../../services/opinion.service";
import { Opinion } from "../../models/opinion";
import { CommonModule } from "@angular/common";
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { ActivatedRoute } from "@angular/router";

@Component({
  selector: "app-opiniones",
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: "./opiniones.component.html",
  styleUrl: "./opiniones.component.css",
})
export class OpinionesComponent implements OnInit {
  @Input() companiaId!: number; // Recibe el ID de la compañía desde el componente padre

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

    // Obtener las opiniones si el companiaId está disponible
    if (this.companiaId) {
      this.obtenerOpiniones();
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
