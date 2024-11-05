import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpInterceptorFn,
  HttpRequest,
} from "@angular/common/http";
import { catchError, Observable, throwError } from "rxjs";

export class AuthInterceptor implements HttpInterceptor {
  constructor() {
    console.log("AuthInterceptor creado");
  }
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    console.log("Interceptor ejecutado");
    const token = localStorage.getItem("authToken");

    if (token) {
      console.log("Token encontrado:", token);
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
    } else {
      console.log("No se encontró token");
    }

    return next.handle(request).pipe(
      catchError((error) => {
        console.error("Error en la solicitud:", error);
        return throwError(error);
      })
    );
  }
}
