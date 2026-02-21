package order_service.enums;


// USER WILL SEE THIS STATUSES
public enum OrderStatus {

    // User placed, waiting for restaurant response or kitchen start
    PLACED,

    // Kitchen / fulfillment stages user cares about
    PREPARING,
    READY,
    PARTIALLY_SERVED,
    SERVED,

    // Cancellation from user POV
    CANCELLATION_REQUESTED,
    CANCELLED,

    // Payment from user POV
    PAYMENT_PENDING,
    PAID,

    // Terminal (closed)
    COMPLETED
}
