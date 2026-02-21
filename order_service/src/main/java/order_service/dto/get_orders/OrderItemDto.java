package order_service.dto.get_orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    private Long orderId;
    private Long orderItemId;
    private Long menuItemId;
    private Integer quantity;
    private String specialInstructions;
    private MenuItemDto menuItem;

}
