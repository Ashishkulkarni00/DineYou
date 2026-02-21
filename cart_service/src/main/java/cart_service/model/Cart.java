package cart_service.model;

import cart_service.enums.CartStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "cartItemList")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    private String userId;

    private String requestId;

    private String keycloakSessionId;

    private String anonymousSessionId;

    private Long restaurantId;

    private String createdAt;

    private String updatedAt;

    @Enumerated
    private CartStatus cartStatus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart", fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<CartItem> cartItemList;

}
