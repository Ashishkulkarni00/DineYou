package restaurant_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import restaurant_service.model.RestaurantDiscount;

import java.util.List;

public interface RestaurantDiscountRepository extends JpaRepository<RestaurantDiscount, Long> {

    @Query("SELECT d FROM RestaurantDiscount d WHERE d.restaurant.restaurantId = :restaurantId")
    List<RestaurantDiscount> findDiscountsByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT d FROM RestaurantDiscount d WHERE d.active = TRUE AND d.restaurant.restaurantId = :restaurantId")
    List<RestaurantDiscount> findActiveDiscountsByRestaurantId(Long restaurantId);
}
