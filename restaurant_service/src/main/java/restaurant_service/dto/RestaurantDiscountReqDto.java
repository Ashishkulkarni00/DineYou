package restaurant_service.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import restaurant_service.enums.DiscountType;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDiscountReqDto {

    private Long restaurantId;
    private String discountName;             // e.g. "Lunch Offer"
    private String description;              // e.g. "20% off on total bill"

    private String discountDate;

    private double discountPercentage;       // e.g. 20.0
    private double maxDiscountAmount;        // optional cap e.g. ₹100

    private String applicableFrom;           // e.g. 2025-07-01
    private String applicableTo;             // e.g. 2025-07-30

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;       // RESTAURANT_WIDE, ITEM_SPECIFIC, CATEGORY_SPECIFIC

    private boolean active;

    private String dayOfWeek;                // "MONDAY", etc.
    private String timeSlot;                 // "14:00-16:00"

}
