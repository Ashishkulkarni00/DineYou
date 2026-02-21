package order_service.dto.get_orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemDto {

    private String itemName;
    private Double itemPrice;
    private String imagePath;
    private String categoryName;

}
