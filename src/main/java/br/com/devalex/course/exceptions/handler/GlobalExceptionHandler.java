package br.com.devalex.course.exceptions.handler;

import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handlerResourceNotFoundException(ResourceNotFoundException ex,
                                                                     HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError error = buildError(
                status,
                ex.getMessage(),
                request,
                null
        );
            return ResponseEntity.status(status).body(error);
    }

    private ApiError buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> errors
    ) {
        return new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                errors
        );
    }
}
