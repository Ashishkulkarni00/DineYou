package order_service.dto.get_orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import order_service.enums.OrderStatus;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupedOrdersDto {

    private Map<String, List<OrderWithItemsDto>> ordersByStatus;

}
