import { Component, OnInit } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { AuthTokenUtil } from "../../utils/auth-token-util";
import { environment } from "../../../environments/environment";
import flatpickr from "flatpickr";
import "flatpickr/dist/flatpickr.css"; // Import CSS
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {
  Chart,
  BarElement,
  CategoryScale,
  LinearScale,
  Title,
  Tooltip,
  Legend,
  BarController,
  DoughnutController,
  ArcElement,
} from "chart.js";
import { SidebarPruebaComponent } from "../../components/sidebar-prueba/sidebar-prueba.component";

Chart.register(
  BarElement,
  BarController,
  CategoryScale,
  LinearScale,
  Title,
  Tooltip,
  Legend,
  DoughnutController,
  ArcElement
);

@Component({
  selector: "app-dashboard",
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarPruebaComponent],
  templateUrl: "./dashboard.component.html",
  styleUrl: "./dashboard.component.css",
})
export class DashboardComponent implements OnInit {
  token: string;
  totalReservas: number = 0;
  totalMonetario: number = 0;
  ventasMensualesData: any;
  reservasCampoData: any;

  private apiUrl: string = environment.NG_APP_URL_API_GENERAL;

  constructor(private authTokenUtil: AuthTokenUtil, private http: HttpClient) {
    this.token = this.authTokenUtil.getToken() || "";
  }

  ngOnInit(): void {
    const fechaActual = new Date().toISOString().split("T")[0];
    this.obtenerReservasDia(fechaActual);
    this.obtenerVentasMensuales();
    this.obtenerCamposReservados(fechaActual);

    flatpickr("#fechaInput", {
      dateFormat: "Y-m-d",
      defaultDate: "today",
      onChange: (selectedDates: Date[], dateStr: string) => {
        this.obtenerReservasDia(dateStr);
        this.obtenerCamposReservados(dateStr);
      },
    });
  }

  async obtenerReservasDia(fecha: string): Promise<void> {
    const apiUrl = `${this.apiUrl}/ventas/reservas/diarias?fecha=${fecha}`;
    const headers = new HttpHeaders().set(
      "Authorization",
      `Bearer ${this.token}`
    );

    try {
      const response: any = await this.http
        .get(apiUrl, { headers })
        .toPromise();
      this.totalReservas = response.totalReservas;
      this.totalMonetario = response.totalMonetario;
      this.crearGraficoDiario(response);
    } catch (error) {
      console.error("Error al obtener los datos de reservas diarias", error);
    }
  }

  async obtenerVentasMensuales(): Promise<void> {
    const apiUrl = `${this.apiUrl}/ventas`;
    const headers = new HttpHeaders().set(
      "Authorization",
      `Bearer ${this.token}`
    );

    try {
      const response: any = await this.http
        .get(apiUrl, { headers })
        .toPromise();
      this.ventasMensualesData = response[0]; // Assuming data is an array
      this.crearGraficoMensual(this.ventasMensualesData);
    } catch (error) {
      console.error("Error al obtener los datos de ventas mensuales", error);
    }
  }

  async obtenerCamposReservados(fecha: string): Promise<void> {
    const apiUrl = `${this.apiUrl}/ventas/reservas/campos-reservados?fecha=${fecha}`;
    const headers = new HttpHeaders().set(
      "Authorization",
      `Bearer ${this.token}`
    );

    try {
      const response: any = await this.http
        .get(apiUrl, { headers })
        .toPromise();
      this.reservasCampoData = response;
      this.crearGraficoCampos(this.reservasCampoData);
    } catch (error) {
      console.error("Error al obtener los datos de campos reservados", error);
    }
  }

  crearGraficoDiario(data: any): void {
    const ctx = document.getElementById(
      "ventasDiariasChart"
    ) as HTMLCanvasElement;
    new Chart(ctx, {
      type: "line",
      data: {
        labels: data.dates,
        datasets: [
          {
            label: "Ventas Diarias",
            data: data.sales,
            backgroundColor: "rgba(75, 192, 192, 0.6)",
            borderColor: "rgba(75, 192, 192, 1)",
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        scales: {
          y: { beginAtZero: true },
        },
      },
    });
  }

  crearGraficoMensual(data: any): void {
    const meses = [
      "Enero",
      "Febrero",
      "Marzo",
      "Abril",
      "Mayo",
      "Junio",
      "Julio",
      "Agosto",
      "Septiembre",
      "Octubre",
      "Noviembre",
      "Diciembre",
    ];
    const reservasMensuales = [
      data.enero || 0,
      data.febrero || 0,
      data.marzo || 0,
      data.abril || 0,
      data.mayo || 0,
      data.junio || 0,
      data.julio || 0,
      data.agosto || 0,
      data.septiembre || 0,
      data.octubre || 0,
      data.noviembre || 0,
      data.diciembre || 0,
    ];

    const ctx = document.getElementById(
      "ventasMensualesChart"
    ) as HTMLCanvasElement;
    new Chart(ctx, {
      type: "bar",
      data: {
        labels: meses,
        datasets: [
          {
            label: `Ventas por Mes (${data.companiaNombre})`,
            data: reservasMensuales,
            backgroundColor: "rgba(75, 192, 192, 0.6)",
            borderColor: "rgba(75, 192, 192, 1)",
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        scales: {
          y: { beginAtZero: true },
        },
      },
    });
  }

  crearGraficoCampos(data: any): void {
    const campos = data.map((item: any) => item.campo);
    const reservasPorCampo = data.map((item: any) => item.totalReservas);

    const ctx = document.getElementById(
      "reservasCampoChart"
    ) as HTMLCanvasElement;
    new Chart(ctx, {
      type: "doughnut",
      data: {
        labels: campos,
        datasets: [
          {
            label: "Reservas por Campo",
            data: reservasPorCampo,
            backgroundColor: [
              "#FF5733",
              "#33FF57",
              "#3357FF",
              "#FF33A1",
              "#FFD700",
            ],
            borderColor: "rgba(255, 255, 255, 0.8)",
            borderWidth: 2,
          },
        ],
      },
      options: {
        responsive: true,
      },
    });
  }
}
