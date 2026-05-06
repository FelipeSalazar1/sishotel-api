package com.fiap.sishotel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static String path(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        FieldError fe = ex.getBindingResult().getFieldError();
        String msg = fe != null && fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Dados inválidos";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorBodies.of(HttpStatus.BAD_REQUEST, "Bad Request", msg, path(req)));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorBodies.of(HttpStatus.BAD_REQUEST, "Bad Request", "Corpo da requisição inválido", path(req)));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorBodies.of(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), path(req)));
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDates(InvalidDateRangeException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorBodies.of(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), path(req)));
    }

    @ExceptionHandler(CapacityExceededException.class)
    public ResponseEntity<Map<String, Object>> handleCapacity(CapacityExceededException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorBodies.of(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), path(req)));
    }

    @ExceptionHandler(RoomUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleUnavailable(RoomUnavailableException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorBodies.of(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), path(req)));
    }

    @ExceptionHandler(InvalidReservationStateException.class)
    public ResponseEntity<Map<String, Object>> handleState(InvalidReservationStateException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorBodies.of(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), path(req)));
    }

    @ExceptionHandler(RoomHasReservationsException.class)
    public ResponseEntity<Map<String, Object>> handleRoomHasRes(RoomHasReservationsException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorBodies.of(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), path(req)));
    }

    @ExceptionHandler(DuplicateGuestException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateGuestException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorBodies.of(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), path(req)));
    }

    @ExceptionHandler(GuestHasReservationsException.class)
    public ResponseEntity<Map<String, Object>> handleGuestHasRes(GuestHasReservationsException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorBodies.of(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), path(req)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(AccessDeniedException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiErrorBodies.of(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), path(req)));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorBodies.of(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), path(req)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, WebRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorBodies.of(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Erro inesperado no servidor", path(req)));
    }
}
