import { CommonModule } from '@angular/common';
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-carousel',
  standalone: true,
  imports: [
    MatButtonModule,
    CommonModule
  ],
  templateUrl: './carousel.component.html',
  styleUrls: ['./carousel.component.css']
})
export class CarouselComponent implements OnInit, OnDestroy {
  @Input() images: string[] = [];
  currentIndex: number = 0;
  private interval: any;
  fadeClass: string = 'fade-in';

  num: number = 0;
  h: string = "hola";

  ngOnInit() {
    this.startCarousel();
  }

  ngOnDestroy() {
    clearInterval(this.interval);
  }

  startCarousel() {
    this.interval = setInterval(() => {
      this.nextImage();
    }, 5000);
  }

  nextImage() {
    this.fadeClass = 'desvanecimiento'; // Reinicia la clase de animación
    
    setTimeout(() => {
      this.currentIndex = (this.currentIndex + 1) % this.images.length;
      this.fadeClass = 'desvanecimiento-top'; // Vuelve a aplicar el desvanecimiento a la nueva imagen
    }, 1500); // Breve espera para que la clase se reactive
  }
}