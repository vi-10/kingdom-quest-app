package app.config;

import app.exception.ApplicationException;
import app.model.dto.event.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(ApplicationException.class)
    public ModelAndView handleApplicationException(ApplicationException e) {
        log.error( "ApplicationException occurred: {}", e.getMessage(), e );
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", e.getMessage());
        modelAndView.addObject("errorCode", e.getErrorCode());
        modelAndView.addObject("errorTitle", e.getErrorTitle());
        return modelAndView; }

    @ExceptionHandler(FeignException.FeignClientException.class)
    public ModelAndView handleFeignException(FeignException ex) throws IOException {
        log.error("FeignException occurred: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = objectMapper.readValue(ex.contentUTF8(), ErrorResponseDTO.class);

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject( "errorMessage", errorResponse.getMessage());
        modelAndView.addObject( "errorCode", errorResponse.getErrorCode());
        modelAndView.addObject( "errorTitle", errorResponse.getErrorTitle());

        return modelAndView;
    }

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
