package restaurant_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LandingPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long landingPageId;

    private String title;

    private String description;

    private Float discountPercentage;

    private Float rating;

    private Long reviewCount;

    private String imagePath;

    private Integer sortOrder;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "restaurantId")
    private Restaurant restaurant;

    @Override
    public String toString() {
        return "LandingPage{" +
                "landingPageId=" + landingPageId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", discountPercentage=" + discountPercentage +
                ", rating=" + rating +
                ", reviewCount=" + reviewCount +
                ", imagePath='" + imagePath + '\'' +
                ", sortOrder=" + sortOrder +
                ", active=" + active +
                '}';
    }
}
