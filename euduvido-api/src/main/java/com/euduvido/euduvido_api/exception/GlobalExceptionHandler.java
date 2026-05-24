package com.euduvido.euduvido_api.exception;

import com.euduvido.euduvido_api.application.exception.AiValidationException;
import com.euduvido.euduvido_api.application.exception.EvidenceValidationException;
import com.euduvido.euduvido_api.application.services.EvidenceValidationResult;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Bean Validation (@Valid) → 400 com lista de campos inválidos */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ErrorResponse.FieldError(e.getField(), e.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(
                ErrorResponse.of(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                        "Validação falhou: " + fieldErrors.size() + " campo(s) inválido(s)", fieldErrors));
    }

    /** Constraint Violation (path/query params) → 400 */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(v -> new ErrorResponse.FieldError(
                        v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        return ResponseEntity.badRequest().body(
                ErrorResponse.of(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                        "Parâmetro(s) inválido(s)", fieldErrors));
    }

    /** Corpo JSON inválido / campo de tipo errado → 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.of(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST",
                        "Corpo da requisição inválido ou mal-formado"));
    }

    /** Parâmetro obrigatório ausente → 400 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.of(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER",
                        "Parâmetro obrigatório ausente: " + ex.getParameterName()));
    }

    /** Regras de negócio (IllegalArgument) → 400 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.of(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", ex.getMessage()));
    }

    /** Recurso não encontrado → 404 */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Recurso não encontrado";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of(HttpStatus.NOT_FOUND, "NOT_FOUND", message));
    }

    /** Método HTTP não suportado → 405 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                ErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED",
                        "Método " + ex.getMethod() + " não suportado neste endpoint"));
    }

    /** Violação de transição de estado (IllegalState) → 409 */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.of(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage()));
    }

    /** Acesso negado (rota protegida sem permissão) → 403 */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.of(HttpStatus.FORBIDDEN, "FORBIDDEN", "Acesso negado"));
    }

    /** Conteúdo de evidência inválido (400) ou impróprio (450) */
    @ExceptionHandler(EvidenceValidationException.class)
    public ResponseEntity<EvidenceValidationResult> handleEvidenceValidation(EvidenceValidationException ex) {
        return ResponseEntity.status(ex.getResult().status()).body(ex.getResult());
    }

    /** Rejeição da validação IA → 422 */
    @ExceptionHandler(AiValidationException.class)
    public ResponseEntity<ErrorResponse> handleAiValidation(AiValidationException ex) {
        Map<String, Object> details = Map.of(
                "confidence", ex.getResult().confidence(),
                "issues", ex.getResult().issues());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ErrorResponse.of(HttpStatus.UNPROCESSABLE_ENTITY, "AI_VALIDATION_FAILED",
                        ex.getMessage(), details));
    }

    /** Fallback — erros não mapeados → 500 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Erro não tratado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                        "Erro interno do servidor"));
    }
}
