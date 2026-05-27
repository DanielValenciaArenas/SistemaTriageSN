package co.edu.uniquindio.sistematriage.exception;

import co.edu.uniquindio.sistematriage.dto.response.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Maneja recursos no encontrados en la base de datos.
     * @param ex excepción lanzada cuando un ID no existe
     * @return respuesta HTTP 404 con mensaje descriptivo
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO("NOT_FOUND", ex.getMessage()));
    }

    /*
     * Maneja violaciones de reglas de negocio del dominio.
     * @param ex excepción lanzada por el service al violar una regla
     * @return respuesta HTTP 400 con mensaje descriptivo
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusiness(BusinessRuleException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO("BUSINESS_RULE_VIOLATION", ex.getMessage()));
    }

    /*
     * Maneja errores de validación de campos en los DTOs de entrada.
     * Se activa cuando un campo anotado con {@NotBlank} o {@NotNull} no cumple su restricción.
     * @param ex excepción lanzada por Spring al fallar la validación
     * @return respuesta HTTP 400 con lista de campos inválidos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO("VALIDATION_ERROR", msg));
    }

    /*
     * Maneja cualquier excepción no controlada explícitamente.
     * Red de seguridad para errores inesperados del sistema.
     * @param ex excepción genérica no anticipada
     * @return respuesta HTTP 500 sin exponer detalles internos
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("INTERNAL_ERROR", "Error interno del servidor"));
    }
}
