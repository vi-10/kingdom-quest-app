package app.config;

import app.exception.ApplicationException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ModelAndView handleApplicationException(ApplicationException e) {
        log.error( "ApplicationException occurred: {}", e.getMessage(), e );
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", e.getMessage());
        modelAndView.addObject("errorCode", e.getErrorCode());
        modelAndView.addObject("errorTitle", e.getErrorTitle());
        return modelAndView; }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        log.error( "Unexpected exception occurred: {}", e.getMessage(), e );
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject( "errorMessage", "Unexpected error occurred." );
        modelAndView.addObject( "errorCode", "500" );
        modelAndView.addObject( "errorTitle", "Internal Server Error" );
        return modelAndView;
    }
}
