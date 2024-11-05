import { Component } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { SidebarComponent } from "../../components/sidebar/sidebar.component";

@Component({
  selector: 'app-panel-admin',
  standalone: true,
  imports: [NavbarComponent, SidebarComponent],
  templateUrl: './panel-admin.component.html',
  styleUrl: './panel-admin.component.css'
})
export class PanelAdminComponent {

}
