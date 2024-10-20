package com.cruz_sur.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Manejar todas las excepciones
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllExceptions(Exception ex, Model model) {
        // Capturar el error en el log
        logger.error("Ocurrió un error: ", ex);

        // Agregar un mensaje al modelo
        model.addAttribute("message", "Ocurrió un error inesperado. Por favor, inténtalo de nuevo más tarde.");

        // Redirigir a la página de error
        return new ModelAndView("error");
    }

    // Manejar excepciones específicas si es necesario
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleNullPointerException(NullPointerException ex) {
        logger.error("Null Pointer Exception: ", ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", "Se produjo un error de referencia nula.");
        return modelAndView;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal Argument Exception: ", ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", "Se proporcionó un argumento no válido.");
        return modelAndView;
    }

    // Puedes agregar más manejadores para excepciones específicas según sea necesario
}
