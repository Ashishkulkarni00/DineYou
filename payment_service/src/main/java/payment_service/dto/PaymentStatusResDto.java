package payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import payment_service.enums.PaymentStatus;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentStatusResDto {

    private String paymentReferenceId;

    private PaymentStatus paymentStatus;

    private String timestamp;

    private Long orderId;

    private Long paidAmount;

}
