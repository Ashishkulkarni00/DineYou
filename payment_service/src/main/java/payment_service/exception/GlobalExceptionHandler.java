package payment_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import payment_service.dto.APIResponse;
import payment_service.dto.ErrorDetails;
import payment_service.dto.ValidationError;
import payment_service.enums.ErrorCode;
import payment_service.util.DateTimeUtil;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<?>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> new ValidationError(err.getField(), err.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest()
                .body(new APIResponse<>(
                        false,
                        "Field validation failed",
                        null,
                        dateTimeUtil.getDateTime(),
                        request.getAttribute("requestId").toString(),
                        new ErrorDetails("VAL_400_2", "Field validation failed",
                                errors.stream()
                                        .map(e -> e.getField() + ": " + e.getMessage())
                                        .collect(Collectors.joining("; ")),
                                null,
                                errors
                        )
                ));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<APIResponse<Object>> handleAppException(
            ApplicationException ex, HttpServletRequest request) {

        ErrorCode code = ex.getErrorCode();
        ErrorDetails errorDetails = new ErrorDetails(
                code.getCode(),
                code.getMessage(),
                ex.getMessage(),
                ex.getField(),
                null
        );

        APIResponse<Object> response = new APIResponse<>(
                false,
                code.getMessage(),
                null,
                dateTimeUtil.getDateTime(),
                (String) request.getAttribute("requestId"),
                errorDetails
        );

        return new ResponseEntity<>(response, mapErrorCodeToStatus(code));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Object>> handleUnhandled(Exception ex, HttpServletRequest request) {
        ErrorDetails errorDetails = ErrorCode.INTERNAL_SERVER_ERROR.withDetails(ex.getMessage());

        APIResponse<Object> response = new APIResponse<>(
                false,
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                null,
                dateTimeUtil.getDateTime(),
                (String) request.getAttribute("requestId"),
                errorDetails
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    private HttpStatus mapErrorCodeToStatus(ErrorCode code) {
        if (code.name().startsWith("AUTH_")) return HttpStatus.UNAUTHORIZED;
        if (code.name().startsWith("RES_404")) return HttpStatus.NOT_FOUND;
        if (code.name().startsWith("VAL_400") || code.name().startsWith("GEN_400")) return HttpStatus.BAD_REQUEST;
        if (code.name().startsWith("RES_409")) return HttpStatus.CONFLICT;
        if (code.name().startsWith("SVC_503")) return HttpStatus.SERVICE_UNAVAILABLE;
        if (code.name().startsWith("SVC_504")) return HttpStatus.GATEWAY_TIMEOUT;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
