package order_service.dto.order_status_response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusResDto {

    private Long orderId;

    private String placedAt;

    List<OrderItemStatusResDto> itemStatusList;

}
