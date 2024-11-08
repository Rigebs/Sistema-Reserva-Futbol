import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthNotificationService {
  private messageSubject = new BehaviorSubject<string | null>(null);

  // Observable que los componentes pueden suscribirse
  message$ = this.messageSubject.asObservable();

  // Método para emitir un mensaje
  sendMessage(message: string): void {
    this.messageSubject.next(message);
  }
}