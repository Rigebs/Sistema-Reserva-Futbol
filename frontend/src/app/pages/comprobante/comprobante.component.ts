import { Component, OnInit } from "@angular/core";
import { jsPDF } from "jspdf";
import html2canvas from "html2canvas";
import { CommonModule } from "@angular/common";
@Component({
  selector: "app-comprobante",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./comprobante.component.html",
  styleUrl: "./comprobante.component.css",
})
export class ComprobanteComponent implements OnInit {
  ngOnInit(): void {
    this.data = history.state.data;
  }
  data: any;

  descargarPdf() {
    const element = document.getElementById("invoice");

    // Usamos html2canvas para capturar el contenido de la factura
    html2canvas(element!).then((canvas) => {
      const imgData = canvas.toDataURL("image/jpeg");
      const doc = new jsPDF();
      doc.addImage(imgData, "JPEG", 10, 10, 180, 160);
      doc.save("factura.pdf");
    });
  }
}
