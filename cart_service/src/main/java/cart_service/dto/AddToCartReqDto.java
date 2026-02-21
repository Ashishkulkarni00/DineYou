package cart_service.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartReqDto {

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Menu Item ID is required")
    private Long menuItemId;

    @NotNull(message = "Cart ID is required")
    private Long cartId;

    @NotNull(message = "Table ID is required")
    private Long tableId;

    @NotNull(message = "Session ID is required")
    private String sessionId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String notes;

    private Long cartItemId;
}
