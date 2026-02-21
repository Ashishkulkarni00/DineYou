package payment_service.enums;

import payment_service.dto.ErrorDetails;

public enum ErrorCode {

    // 🔐 AUTHENTICATION & AUTHORIZATION
    UNAUTHORIZED("AUTH_401", "Authentication required or token missing"),
    FORBIDDEN("AUTH_403", "Access denied for the requested resource"),
    INVALID_CREDENTIALS("AUTH_401_1", "Invalid username or password"),
    TOKEN_EXPIRED("AUTH_401_2", "Authentication token has expired"),
    TOKEN_INVALID("AUTH_401_3", "Invalid or malformed authentication token"),

    // 🔎 VALIDATION & INPUT
    INVALID_INPUT("VAL_400", "Invalid input provided"),
    MISSING_REQUIRED_FIELD("VAL_400_1", "A required field is missing"),
    FIELD_VALIDATION_FAILED("VAL_400_2", "Field validation failed"),

    // 🔎 RESOURCE ERRORS
    RESOURCE_NOT_FOUND("RES_404", "Requested resource not found"),
    DUPLICATE_RESOURCE("RES_409", "Resource already exists"),
    RESOURCE_CONFLICT("RES_409_1", "Conflict with existing resource"),
    RESOURCE_DEPENDENCY_CONFLICT("RES_409_2", "Cart cannot be deleted because it contains one or more items"),


    // 🧱 DATABASE & SYSTEM ERRORS
    DATABASE_ERROR("SYS_500_1", "A database error occurred"),
    DATA_INTEGRITY_VIOLATION("SYS_500_2", "Data integrity constraint violated"),
    INTERNAL_SERVER_ERROR("SYS_500", "An unexpected error occurred"),


    // 🔗 SERVICE-TO-SERVICE COMMUNICATION
    DOWNSTREAM_SERVICE_ERROR("SVC_503", "A dependent service failed to respond"),
    TIMEOUT("SVC_504", "Request to another service timed out"),
    SERVICE_UNAVAILABLE("SVC_503_1", "Service is currently unavailable"),

    // 🚦 GENERAL ERRORS
    BAD_REQUEST("GEN_400", "Bad request"),
    METHOD_NOT_ALLOWED("GEN_405", "HTTP method not allowed on this endpoint"),
    UNSUPPORTED_MEDIA_TYPE("GEN_415", "Unsupported content type"),


    UNKNOWN_ERROR("GEN_520", "An unknown error occurred"),

    PAYMENT_DECLINED("PAY_001", "Payment was declined by the provider."),
    INSUFFICIENT_FUNDS("PAY_002", "Insufficient funds to complete the transaction."),
    INVALID_CARD_DETAILS("PAY_003", "Provided card details are invalid or expired."),
    PAYMENT_GATEWAY_TIMEOUT("PAY_004", "Payment gateway timed out. Please try again."),
    DUPLICATE_TRANSACTION("PAY_005", "Duplicate transaction detected."),
    UNSUPPORTED_PAYMENT_METHOD("PAY_006", "This payment method is not supported."),
    CURRENCY_NOT_SUPPORTED("PAY_007", "The selected currency is not supported."),
    TRANSACTION_LIMIT_EXCEEDED("PAY_008", "Transaction amount exceeds the allowed limit."),
    PAYMENT_SESSION_EXPIRED("PAY_009", "Payment session expired. Please try again."),
    PAYMENT_AUTHENTICATION_FAILED("PAY_010", "Payment authentication failed. Check your credentials."),
    INVALID_PAYMENT_REQUEST("PAY_011", "Invalid payment request data."),
    REFUND_FAILED("PAY_012", "Refund could not be processed. Try again later."),
    ORDER_NOT_ELIGIBLE_FOR_PAYMENT("PAY_013", "Order is not eligible for payment."),
    ORDER_ALREADY_PAID("PAY_014", "This order has already been paid for."),
    EXTERNAL_PAYMENT_ERROR("PAY_015", "An error occurred with the external payment provider.");


    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    // Build ErrorDetails from exception
    public ErrorDetails withDetails(String exceptionMessage) {
        return new ErrorDetails(code, message, exceptionMessage, null, null);
    }

    public ErrorDetails withDetails(String exceptionMessage, String field) {
        return new ErrorDetails(code, message, exceptionMessage, field,null);
    }
}

