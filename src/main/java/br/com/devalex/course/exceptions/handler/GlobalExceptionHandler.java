package br.com.devalex.course.exceptions.handler;

import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handlerValidationErrors(MethodArgumentNotValidException ex,
                                                            HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        ApiError error = buildError(
                status,
                "Erro de validação",
                request,
                errors
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handlerTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                        HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String paramValue = String.valueOf(ex.getValue());
        String expectedType = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "Tipo desconhecido";
        String message = String.format(
                "Valor inválido '%s' para o parâmetro '%s'. Tipo esperado: %s",
                paramValue,
                ex.getName(),
                expectedType
        );
        ApiError error = buildError(
                status,
                message,
                request,
                null
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiError error = buildError(
                status,
                "Erro interno inesperado",
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
