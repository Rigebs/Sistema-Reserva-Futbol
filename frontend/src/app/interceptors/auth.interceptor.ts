import { HttpInterceptorFn } from "@angular/common/http";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const autoToken = localStorage.getItem("authToken");

  if (autoToken) {
    console.log("Token encontrado:", autoToken);

    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${autoToken}`,
      },
    });

    return next(clonedRequest);
  } else {
    console.log("No se encontró token");
  }

  return next(req);
};
