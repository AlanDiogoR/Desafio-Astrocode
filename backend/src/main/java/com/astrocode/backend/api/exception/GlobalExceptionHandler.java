package com.astrocode.backend.api.exception;

import com.astrocode.backend.config.CorrelationIdFilter;
import com.astrocode.backend.domain.exceptions.AccountNotOwnedException;
import com.astrocode.backend.domain.exceptions.DuplicateAccountNameException;
import com.astrocode.backend.domain.exceptions.CategoryTypeMismatchException;
import com.astrocode.backend.domain.exceptions.EmailAlreadyExistsException;
import com.astrocode.backend.domain.exceptions.EmailNotVerifiedException;
import com.astrocode.backend.domain.exceptions.InsufficientBalanceException;
import com.astrocode.backend.domain.exceptions.InvalidCredentialsException;
import com.astrocode.backend.domain.exceptions.InvalidPasswordException;
import com.astrocode.backend.domain.exceptions.InvalidResetCodeException;
import com.astrocode.backend.domain.exceptions.InvalidTokenException;
import com.astrocode.backend.domain.exceptions.InvalidTransactionSourceException;
import com.astrocode.backend.domain.exceptions.DuplicateCreditCardNameException;
import com.astrocode.backend.domain.exceptions.PaymentRejectedException;
import com.astrocode.backend.domain.exceptions.PlanUpgradeRequiredException;
import com.astrocode.backend.domain.exceptions.ResourceAccessDeniedException;
import com.astrocode.backend.domain.exceptions.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(DuplicateAccountNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateAccountNameException(DuplicateAccountNameException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerifiedException(EmailNotVerifiedException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidResetCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResetCodeException(InvalidResetCodeException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({InvalidTokenException.class, JwtException.class})
    public ResponseEntity<ErrorResponse> handleTokenException(Exception ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ResourceAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessDeniedException(ResourceAccessDeniedException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(AccountNotOwnedException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotOwnedException(AccountNotOwnedException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(CategoryTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleCategoryTypeMismatchException(CategoryTypeMismatchException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidTransactionSourceException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransactionSourceException(InvalidTransactionSourceException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DuplicateCreditCardNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCreditCardNameException(DuplicateCreditCardNameException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(PaymentRejectedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentRejectedException(PaymentRejectedException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(PlanUpgradeRequiredException.class)
    public ResponseEntity<PlanUpgradeErrorResponse> handlePlanUpgradeRequiredException(PlanUpgradeRequiredException ex) {
        var errorResponse = new PlanUpgradeErrorResponse(
                HttpStatus.PAYMENT_REQUIRED.value(),
                ex.getMessage(),
                ex.getFeature(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(errorResponse);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(ObjectOptimisticLockingFailureException ex) {
        var errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflito de concorrência. Tente novamente.",
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        String safeMessage = sanitizeSensitiveMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, safeMessage, OffsetDateTime.now()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        String safeMessage = sanitizeSensitiveMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, safeMessage, OffsetDateTime.now()));
    }

    private String sanitizeSensitiveMessage(String message) {
        if (message == null) return "Requisição inválida";
        String lower = message.toLowerCase();
        if (lower.contains("jwt") || lower.contains("secret") || lower.contains("base64") || lower.contains("token")) {
            return "Configuração inválida. Verifique as variáveis de ambiente.";
        }
        return message;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = String.format("Valor inválido '%s' para o parâmetro '%s'", ex.getValue(), ex.getName());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, msg, OffsetDateTime.now()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst()
                .orElse("Erro de validação");
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, msg, OffsetDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("[VALIDATION ERROR] Campos inválidos: {}", ex.getBindingResult().getAllErrors());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        var errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação nos dados fornecidos",
                OffsetDateTime.now(),
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        log.error("[PARSE ERROR] Body inválido: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, "Corpo da requisição inválido ou JSON malformado", OffsetDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        String correlationId = org.slf4j.MDC.get(CorrelationIdFilter.CORRELATION_ID);
        String method = request != null ? request.getMethod() : "?";
        String uri = request != null ? request.getRequestURI() : "?";
        log.error("[500 ERROR] {} {} correlationId={} exception={} message={}",
                method, uri, correlationId, ex.getClass().getName(), ex.getMessage(), ex);
        var errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                OffsetDateTime.now(),
                correlationId
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    public record ErrorResponse(
            int status,
            String message,
            OffsetDateTime timestamp,
            String correlationId
    ) {
        public ErrorResponse(int status, String message, OffsetDateTime timestamp) {
            this(status, message, timestamp, null);
        }
    }

    public record PlanUpgradeErrorResponse(
            int status,
            String message,
            String feature,
            OffsetDateTime timestamp
    ) {
    }

    public record ValidationErrorResponse(
            int status,
            String message,
            OffsetDateTime timestamp,
            Map<String, String> errors
    ) {
    }
}
