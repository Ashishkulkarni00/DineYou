package cart_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemResDto {


    private Long itemId;

    private String itemName;

    private String itemDescription;

    private Double itemPrice;

    private String itemImageUrl;

    private boolean available;

    private Long availableQuantity;

    private String createdAt;

    private String updatedAt;


}
