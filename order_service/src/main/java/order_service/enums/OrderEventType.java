package order_service.enums;

//THESE WILL BE FOR AUDITING AND ANALYSIS
public enum OrderEventType {

    // Creation
    ORDER_PLACED,

    // Restaurant/staff actions
    ORDER_ACCEPTED,
    ORDER_REJECTED,              // optional but very useful for audit/debug
    ORDER_PREPARATION_STARTED,   // better than just PREPARING as an event
    ORDER_READY,
    ORDER_PARTIALLY_SERVED,
    ORDER_SERVED,

    // Cancellation flow
    CANCELLATION_REQUESTED_BY_CUSTOMER,
    CANCELLATION_APPROVED_BY_RESTAURANT,  // optional if you have approval flow
    ORDER_CANCELLED,

    // Payment flow (keep separate from status but still an event)
    PAYMENT_INITIATED,
    PAYMENT_SUCCEEDED,
    PAYMENT_FAILED,

    // Closure
    ORDER_COMPLETED
}
