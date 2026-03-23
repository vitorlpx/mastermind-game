import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

interface BackendErrorPayload {
  message?: string;
}

function resolveMessage(error: HttpErrorResponse): string {
  const payload = error.error as BackendErrorPayload | string | null;

  if (payload && typeof payload === 'object' && typeof payload.message === 'string' && payload.message.trim()) {
    return payload.message;
  }

  if (typeof payload === 'string' && payload.trim()) {
    return payload;
  }

  if (error.status === 0) {
    return 'Não foi possível conectar ao servidor.';
  }

  if (error.status === 401) {
    return 'Não autorizado. Faça login novamente.';
  }

  if (error.status === 403) {
    return 'Acesso negado para esta operação.';
  }

  if (error.status === 404) {
    return 'Recurso não encontrado.';
  }

  if (error.status >= 500) {
    return 'Erro interno do servidor. Tente novamente mais tarde.';
  }

  if (typeof error.message === 'string' && error.message.trim()) {
    return error.message;
  }

  return 'Erro ao processar a requisição.';
}

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: unknown) => {
      if (!(error instanceof HttpErrorResponse)) {
        return throwError(() => error);
      }

      const message = resolveMessage(error);
      const normalizedError = new HttpErrorResponse({
        headers: error.headers,
        status: error.status,
        statusText: error.statusText,
        url: error.url ?? undefined,
        error: {
          message,
          originalError: error.error
        }
      });

      return throwError(() => normalizedError);
    })
  );
};
