import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { ReservarCampoComponent } from './pages/reservar-campo/reservar-campo.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'home', component: HomeComponent },
    { path: 'reservar-campo', component: ReservarCampoComponent },
];
