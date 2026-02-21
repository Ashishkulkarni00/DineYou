package menucard_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuCardReqDto {

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotEmpty(message = "At least one category is required")
    private List<CategoryDTO> categories;

    @Data
    @NoArgsConstructor
    public static class CategoryDTO {
        @NotBlank(message = "Category name is required")
        private String categoryName;
        private String description;

        @NotEmpty(message = "Each category must have at least one item")
        private List<ItemDTO> items;
    }

    @Data
    @NoArgsConstructor
    public static class ItemDTO {
        @NotBlank(message = "Item name is required")
        private String itemName;
        private String itemDescription;

        @NotNull(message = "Item price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        private Double itemPrice;

        private String itemImageUrl;
        private boolean available = true;
    }

}
