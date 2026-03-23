import { HttpErrorResponse } from '@angular/common/http';

interface BackendErrorPayload {
  message?: string;
}

const DEFAULT_ERROR_MESSAGE = 'Erro ao processar a requisição.';

export function getBackendErrorMessage(error: unknown, fallbackMessage = DEFAULT_ERROR_MESSAGE): string {
  if (!(error instanceof HttpErrorResponse)) {
    return fallbackMessage;
  }

  const payload = error.error as BackendErrorPayload | string | null;

  if (payload && typeof payload === 'object' && typeof payload.message === 'string' && payload.message.trim()) {
    return payload.message;
  }

  if (typeof payload === 'string' && payload.trim()) {
    return payload;
  }

  if (typeof error.message === 'string' && error.message.trim()) {
    return error.message;
  }

  return fallbackMessage;
}
