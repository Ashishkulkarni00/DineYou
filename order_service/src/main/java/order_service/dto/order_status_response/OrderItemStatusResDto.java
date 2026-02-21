package order_service.dto.order_status_response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import order_service.enums.OrderEventType;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemStatusResDto {

    private Long orderItemId;

    private Long menuItemId;

    private String itemName;

    private String itemDescription;

    private Double itemPrice;

    private String itemImageUrl;

    private OrderEventType status;

}
