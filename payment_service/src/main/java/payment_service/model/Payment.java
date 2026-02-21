package payment_service.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import payment_service.enums.PaymentStatus;

@Entity
@Data   
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @NotNull(message = "order ID is required")
    private Long orderId;

    @NotNull(message = "restaurant ID is required")
    private Long restaurantId;

    private String keycloakSessionId;

    private String anonymousSessionId;

    private String requestId;

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
