package order_service.dto;


import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class UpdateOrderEventReqDto {

    @NotNull(message = "order ID is required")
    private Long orderId;

    @NotNull(message = "order item ID is required")
    private Long orderItemId;

    @NotNull(message = "event type is required")
    private String eventType;

}
