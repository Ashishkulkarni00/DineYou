package restaurant_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LandingPageResDto {

    private Long restaurantId;
    private String restaurantName;
    private String location;
    private String address;
    private Float averageRating;
    private Long totalReviews;
    private String logoImagePath;

    // Banner section
    private List<BannerDto> banners;

    // Popular items section
    private List<MenuItemResDto> popularItems;

    // Optional: last updated timestamp
    private String lastUpdated;

    // DTO for Banner section
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BannerDto {
        private Long landingPageId;
        private String title;
        private String description;
        private Float discountPercentage;
        private Float rating;
        private Long reviewCount;
        private String imagePath;
        private Integer sortOrder;
        private Boolean active;
    }
}
