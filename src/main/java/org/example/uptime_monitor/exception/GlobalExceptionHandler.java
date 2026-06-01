package org.example.uptime_monitor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Bu anotasyon tüm controller sınıflarındaki hataları dinlemesini sağlar

public class GlobalExceptionHandler {
    // 1. GENEL HATALARI YAKALAMA (Örn: RuntimeException - "Bu isimde bir web sitesi zaten kayıtlı!" uyarısı için)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException exception) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("error", exception.getMessage());
        errors.put("status", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 2. VALIDATION (DOĞRULAMA) HATALARINI YAKALAMA
    // DTO'da yazdığımız @NotBlank veya @Pattern kurallarına uyulmazsa bu metot devreye girer
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());

        // Hangi alanda ne hatası olduğunu tek tek listeliyoruz
        Map<String, String> validationDetails = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationDetails.put(fieldName, errorMessage);
        });

        errors.put("validationErrors", validationDetails);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 3. SİSTEMSEL VE ÖNGÖRÜLEMEYEN DİĞER TÜM HATALAR İÇİN (Erişim reddi, null pointer vb.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception exception) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("error", "Sistemsel bir hata oluştu: " + exception.getMessage());
        errors.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

