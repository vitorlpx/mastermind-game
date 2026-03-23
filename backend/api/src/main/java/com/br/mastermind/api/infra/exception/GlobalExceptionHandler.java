package com.br.mastermind.api.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.br.mastermind.api.dto.MessageResponseDTO;

  @RestControllerAdvice
  public class GlobalExceptionHandler {
    
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<MessageResponseDTO> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<MessageResponseDTO> handleInvalidCredentials(InvalidCredentialsException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
      String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponseDTO(errorMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MessageResponseDTO> handleMalformedRequest(HttpMessageNotReadableException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponseDTO("Payload inválido"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponseDTO> handleRuntimeException(RuntimeException ex) {
      String errorMessage = ex.getMessage() != null && !ex.getMessage().isBlank()
          ? ex.getMessage()
          : "Erro ao processar a requisição";

      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponseDTO(errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDTO> handleUnexpectedException(Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new MessageResponseDTO("Erro interno do servidor"));
    }

  }
