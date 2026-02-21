package order_service.dto.get_orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import order_service.model.OrderItem;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderWithItemsDto {
    private Long orderId;
    private List<OrderItemDto> orderItems;
}

