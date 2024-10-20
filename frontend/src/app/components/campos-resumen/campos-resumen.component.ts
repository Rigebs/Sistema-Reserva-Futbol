import { NgForOf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-campos-resumen',
  standalone: true,
  imports: [MatCardModule, MatIconModule, NgForOf],
  templateUrl: './campos-resumen.component.html',
  styleUrl: './campos-resumen.component.css',
  providers: [DatePipe]
})
export class CamposResumenComponent {
  @Input() reservas: any[] = [];

  constructor(private datePipe: DatePipe) {}

  formatDate(date: Date): string {
    return this.datePipe.transform(date, 'dd/MM/yyyy') || '';
  }
}
