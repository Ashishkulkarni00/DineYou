package cart_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCartReqDto {

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

}
