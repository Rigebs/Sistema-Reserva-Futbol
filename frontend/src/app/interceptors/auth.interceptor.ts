import { HttpInterceptorFn } from "@angular/common/http";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const autoToken = localStorage.getItem("authToken");

  if (autoToken) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${autoToken}`,
      },
    });

    return next(clonedRequest);
  }

  return next(req);
};
