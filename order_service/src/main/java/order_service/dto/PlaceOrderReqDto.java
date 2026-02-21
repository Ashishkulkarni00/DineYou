package order_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlaceOrderReqDto {

    @NotNull(message = "restaurant ID is required")
    private Long restaurantId;

    @NotNull(message = "user ID is required")
    private String userId;

    @NotNull(message = "cart ID is required")
    private Long cartId;

    @NotNull(message = "order items are required")
    private List<OrderItemDto> orderItems;



}
