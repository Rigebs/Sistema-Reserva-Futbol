package com.cruz_sur.api.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Manejo de excepciones generales
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllExceptions(Exception ex, Model model) {
        logger.error("Ocurrió un error: ", ex);
        model.addAttribute("message", "Ocurrió un error inesperado. Por favor, inténtalo de nuevo más tarde.");
        return new ModelAndView("error");
    }

    // Manejo específico de ExpiredJwtException
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // Ajusta el código de estado según sea necesario
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {
        logger.warn("JWT Expired Exception: {}", ex.getMessage());
        String errorMessage = "El token ha expirado. Por favor, inicie sesión nuevamente.";
        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    // NullPointerException específico
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleNullPointerException(NullPointerException ex) {
        logger.error("Null Pointer Exception: ", ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", "Se produjo un error de referencia nula.");
        return modelAndView;
    }

    // IllegalArgumentException específico
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal Argument Exception: ", ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", "Se proporcionó un argumento no válido.");
        return modelAndView;
    }

    // Manejo específico de RuntimeException, sin stack trace detallado
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.warn("Runtime Exception: {}", ex.getMessage());
        String errorMessage = "Ocurrió un error al procesar la solicitud: " + ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // Manejo específico de DataIntegrityViolationException
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.error("Data Integrity Violation Exception: ", ex);
        String errorMessage = "Violación de integridad de datos. Puede haber conflicto con datos existentes.";
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CampoAlreadyReservedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleCampoAlreadyReservedException(CampoAlreadyReservedException ex) {
        logger.warn("Campo already reserved: {}", ex.getMessage());
        String errorMessage = ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    // Manejo específico de EntityNotFoundException (usualmente para entidades de JPA no encontradas)
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.error("Entity Not Found Exception: ", ex);
        String errorMessage = "La entidad solicitada no fue encontrada en el sistema.";
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    // Manejo específico de MethodArgumentNotValidException (errores de validación en los argumentos)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Validation Exception: ", ex);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Custom exception for already reserved field
    public static class CampoAlreadyReservedException extends RuntimeException {
        public CampoAlreadyReservedException(Long campoId, Long horarioId) {
            super("Campo with ID " + campoId + " is already reserved at horario ID " + horarioId);
        }
    }
}
