package br.com.doc_voice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ApiError apiError;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage(), errors);
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler(ConvertException.class)
    public ResponseEntity<ApiError> handleConvertException(ConvertException e) {
        apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiError> handleInvalidFileException(InvalidFileException e) {
        apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler(ExtractionException.class)
    public ResponseEntity<ApiError> handleExtractionException(ExtractionException e) {
        apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler(GeminiCallException.class)
    public ResponseEntity<ApiError> handleGeminiCallException(GeminiCallException e) {
        apiError = new ApiError(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }
}
