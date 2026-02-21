package payment_service.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import payment_service.enums.PaymentStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentReqDto {

    private Long paymentId;

    @NotNull(message = "order ID is required")
    private List<Long> orderIds;

    @NotNull(message = "restaurant ID is required")
    private Long restaurantId;

    @NotNull(message = "user ID is required")
    private String userId;

    private String gatewayName;

    private String gatewaySessionId;

    @Column(columnDefinition = "TEXT")
    private String gatewaySessionUrl;

    private String paymentReferenceId;

    private String paymentMethod; // e.g., "UPI", "CASH", "CARD", "WALLET"

    @NotNull(message = "amount is required")
    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // PENDING, SUCCESS, FAILED

    private String initialisedAt;

    private String updatedAt;

    @NotNull(message = "currency is required")
    @NotEmpty(message = "currency cannot be blank")
    private String currency;

}
