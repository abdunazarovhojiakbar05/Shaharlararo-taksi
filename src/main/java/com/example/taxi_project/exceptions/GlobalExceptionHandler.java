package com.example.taxi_project.exceptions;

import com.example.taxi_project.dto.error.ErrorResponse;
import com.example.taxi_project.model.ErrorLog;
import com.example.taxi_project.repository.ErrorLogRepository;
import com.example.taxi_project.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
 import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;


 import java.time.LocalDateTime;
import java.util.UUID;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorLogRepository errorLogRepository;
    private final MessageService messageService;

     @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, messageService.getMessage(ex.getMessage()), request.getRequestURI());
    }

     @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Validation xatosi");
        return build(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, messageService.getMessage(ex.getMessage()), request.getRequestURI());
    }

     @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

     @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationEx(
            ValidationException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

     @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedEx(
            UnauthorizedException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            AuthenticationException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED,
                messageService.getMessage("auth.token.invalid"),
                request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN,
                messageService.getMessage("auth.access.denied"),
                request.getRequestURI());
    }

     @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(
            ResponseStatusException ex, HttpServletRequest request) {
        return build(HttpStatus.valueOf(ex.getStatusCode().value()),
                ex.getReason(), request.getRequestURI());
    }

     @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        ex.printStackTrace();
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, String path) {
        ErrorResponse response = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();

        try {
            ErrorLog log = new ErrorLog();
            log.setError_message(message);
            log.setPath(path);
            log.setStatus(status.value());
            log.setCreated_at(LocalDateTime.now());
            log.setUser_id(getCurrentUserId());

            errorLogRepository.save(log);


        } catch (Exception e) {
            System.err.println("Xatolikni bazaga yozishda xato bo'ldi: " + e.getMessage());
        }
        return ResponseEntity.status(status).body(response);
    }


    private UUID getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()
                    && !(authentication instanceof AnonymousAuthenticationToken)) {

                CustomUserDetails principal = (CustomUserDetails    ) authentication.getPrincipal();
                return principal.getId();
            }
        } catch (Exception e) {
        }
        return null;
    }
}