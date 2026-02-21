package cart_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCartReqDto {

    @NotNull(message = "CartItem ID is required")
    private Long cartItemId;

    @Min(value = 0, message = "Quantity must be zero or more")
    private Integer quantity;

    private String preparationNote;

    @NotNull(message = "session ID is required")
    private String sessionId;

}
