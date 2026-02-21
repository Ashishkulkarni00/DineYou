package restaurant_service.exception;
import restaurant_service.enums.ErrorCode;

public class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String field;

    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.field = null;
    }

    public ApplicationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.field = null;
    }

    public ApplicationException(ErrorCode errorCode, String message, String field) {
        super(message);
        this.errorCode = errorCode;
        this.field = field;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getField() {
        return field;
    }
}
