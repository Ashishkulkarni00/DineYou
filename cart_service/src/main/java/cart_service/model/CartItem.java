package cart_service.model;

import cart_service.enums.CartItemStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "cartItem")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "cart")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    // @Column(unique = true, nullable = false)
    private Long menuItemId;

    private Long tableId;

    private String anonymousSessionId;

    private String requestId;

    private String keycloakSessionId;

    @ManyToOne
    @JoinColumn(name = "cartId")
    @JsonBackReference
    private Cart cart;

    private int quantity;

    private String notes;

    @Enumerated
    private CartItemStatus itemStatus;

    private String addedAt;

    private String updatedAt;

}
