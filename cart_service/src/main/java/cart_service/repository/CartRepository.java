package cart_service.repository;

import cart_service.enums.CartStatus;
import cart_service.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface CartRepository extends JpaRepository<Cart,Long> {


    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.cartStatus = :cartStatus")
    Optional<Cart> findCartByUserId(@Param("userId") String userId, @Param("cartStatus") CartStatus status);

    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.restaurantId = :restaurantId AND c.cartStatus = :status")
    Optional<Cart> findByUserIdAndRestaurantIdAndStatus(@Param("userId") String userId,
                                              @Param("restaurantId") Long restaurantId,
                                              @Param("status") CartStatus status);

}
