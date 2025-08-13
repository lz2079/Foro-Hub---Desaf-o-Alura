package alura.desafio.foro.infra.exception;

import io.jsonwebtoken.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Error de validación en los datos enviados.",
                errors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String mensaje = "Error de integridad de datos. Es posible que estés intentando crear un recurso que ya existe (ej: email duplicado).";

        if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")) {
            if (ex.getMessage().contains("email")) {
                mensaje = "Ya existe un usuario registrado con ese email.";
            } else {
                mensaje = "Ya existe un recurso con ese valor único.";
            }
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                mensaje,
                null
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "El recurso solicitado no fue encontrado.",
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Credenciales inválidas. Por favor, verifica tu email y contraseña.",
                null
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }


    @ExceptionHandler({
            ExpiredJwtException.class,
            MalformedJwtException.class,
            UnsupportedJwtException.class,
            IllegalArgumentException.class,
            SignatureException.class
    })
    public ResponseEntity<ErrorResponse> handleJwtException(Exception ex) {
        String mensaje = "Token JWT inválido o expirado.";
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        if (ex instanceof ExpiredJwtException) {
            mensaje = "El token JWT ha expirado.";
        } else if (ex instanceof MalformedJwtException) {
            mensaje = "El token JWT está mal formado.";
        } else if (ex instanceof UnsupportedJwtException) {
            mensaje = "El token JWT no es soportado.";
        } else if (ex instanceof IllegalArgumentException) {
            if (ex.getMessage() != null && ex.getMessage().contains("key")) {
                mensaje = "Error en la clave secreta JWT. Debe tener al menos 256 bits (32 caracteres).";
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            } else {
                mensaje = "Argumento ilegal proporcionado al procesar el token JWT.";
            }
        } else if (ex instanceof SignatureException) {
            mensaje = "Firma del token JWT inválida.";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                null
        );

        return ResponseEntity.status(status).body(errorResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ha ocurrido un error inesperado en el servidor.",
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    public static class ErrorResponse {
        private final LocalDateTime timestamp;
        private final int status;
        private final String error;
        private final String message;
        private final List<ValidationError> errors;

        public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, List<ValidationError> errors) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.errors = errors;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
        public List<ValidationError> getErrors() { return errors; }
    }

    public static class ValidationError {
        private final String field;
        private final String message;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() { return field; }
        public String getMessage() { return message; }
    }
}