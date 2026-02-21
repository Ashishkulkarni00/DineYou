package order_service.dto.order_status_response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import order_service.dto.get_orders.OrderItemDto;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderWithStatusGroupsDto {

    private Long orderId;
    private Long tableId;
    private String  createdAt;
    private String updatedAt;
    private Map<String, List<OrderItemDto>> statusGroups;

}
