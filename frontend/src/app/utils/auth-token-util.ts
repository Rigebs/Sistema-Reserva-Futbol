import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root",
})
export class AuthTokenUtil {
  private readonly tokenKey = "authToken";

  constructor() {}

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  removeToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  hasToken(): boolean {
    return !!this.getToken();
  }

  getUserFromToken(): string {
    const user = this.decodeToken().sub;
    return user;
  }

  isTokenValid(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      const expiration = payload.exp * 1000; // Asumiendo que el campo "exp" está en segundos
      return Date.now() < expiration;
    } catch (e) {
      return false;
    }
  }

  decodeToken(): any | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      return payload;
    } catch (e) {
      console.error("Error decoding token:", e);
      return null;
    }
  }

  isAdmin(): boolean {
    const payload = this.decodeToken();
    return payload?.roles && payload.roles.includes("ROLE_ADMIN");
  }

  isCompania(): boolean {
    const payload = this.decodeToken();
    return payload?.roles && payload.roles.includes("ROLE_COMPANIA");
  }

  isEspera(): boolean {
    const payload = this.decodeToken();
    return payload?.roles && payload.roles.includes("ROLE_ESPERA");
  }

  isUser(): boolean {
    const payload = this.decodeToken();
    return payload?.roles && payload.roles.includes("ROLE_USER");
  }
}
