package com.francisco.commons.exceptions;

import com.francisco.commons.dto.CustomErrorsResponse;
import feign.FeignException;
import feign.RetryableException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomErrorsResponse> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Violación de restricción: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(new CustomErrorsResponse(HttpStatus.BAD_REQUEST.value(), "Violación de restricción: " + e.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorsResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación en los datos enviados");
        log.error("Error de validación de argumentos: {}", mensaje);
        return ResponseEntity.badRequest()
                .body(new CustomErrorsResponse(HttpStatus.BAD_REQUEST.value(), mensaje));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomErrorsResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Error en la petición: {}", e.getMessage());
        return ResponseEntity.badRequest().body(
                new CustomErrorsResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage())
        );
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CustomErrorsResponse> handleIllegalStateException(IllegalStateException e) {
        log.error("Error en el estado de la petición: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new CustomErrorsResponse(HttpStatus.CONFLICT.value(), e.getMessage())
        );
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<CustomErrorsResponse> handleNoSuchElementException(NoSuchElementException e) {
        log.warn("No se encontró recurso: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CustomErrorsResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CustomErrorsResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("No se encontró recurso estático: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CustomErrorsResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<CustomErrorsResponse> handleRecursoNoEncontradoException(RecursoNoEncontradoException e) {
        log.warn("No se encontró el recurso: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CustomErrorsResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }
    @ExceptionHandler(FeignException .class)
    public ResponseEntity<CustomErrorsResponse> handleGenericFeignException(FeignException e) {
        log.error("Error en la comunicación Feign: " + e.getMessage());

        int status = e.status() > 0 ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = switch (status) {
            case 400 -> "Solicitud incorrecta al servicio remoto.";
            case 401 -> "No autorizado para acceder al servicio remoto.";
            case 403 -> "Acceso prohibido al servicio remoto.";
            case 404 -> "Recurso no encontrado en el servicio remoto.";
            case 409 -> "Conflicto: el recurso tiene dependencias activas.";
            case 503 -> "Servicio remoto no disponible.";
            default -> "Error al comunicarse con el servicio remoto.";
        };
        CustomErrorsResponse response = new CustomErrorsResponse(status, message);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<CustomErrorsResponse> handleRetryable(RetryableException e) {
        log.error("Servicio remoto no disponible o no responde: " + e.getMessage());
        CustomErrorsResponse response = new CustomErrorsResponse(HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Servicio remoto no disponible o no responde");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorsResponse> handleGeneralException(Exception e) {
        log.error("Error interno del servidor: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomErrorsResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error interno del servidor. Por favor, contacte al administrador."));
    }
}

