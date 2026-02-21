package menucard_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name must be under 100 characters")
    private String itemName;

    @Size(max = 500, message = "Description must be under 500 characters")
    private String itemDescription;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double itemPrice;

    private boolean available;

    private boolean isPopular;
    
    private boolean isVegetarian;

    private Integer minimumPreparationTime;

    private Long soldCount;

    private String imagePath;

    private Float discountPercentage;

    private Float rating;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long availableQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    @JsonBackReference
    private ItemCategory category;

    private String createdAt;

    private String updatedAt;

}
