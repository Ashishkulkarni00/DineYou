package cart_service.dto;
import cart_service.enums.CartItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCartItemStatusReqDto {

    @NotNull(message = "cart item ID is required")
    private Long cartItemId;

    @NotNull(message = "Cart item status is required")
    String cartItemStatus;

}
