package order_service.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    @NotNull(message = "menu item ID is required")
    private Long menuItemId;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "At least one item should be ordered")
    private int quantity;

    private String specialInstructions;

}
