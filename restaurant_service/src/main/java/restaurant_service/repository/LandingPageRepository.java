package restaurant_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import restaurant_service.model.LandingPage;

import java.util.List;
import java.util.Optional;

public interface LandingPageRepository extends JpaRepository<LandingPage, Long> {

    @Query(value = "SELECT * FROM landing_page lp WHERE lp.restaurant_id = :restaurantId AND lp.active = 1", nativeQuery = true)
    Optional<LandingPage> findByRestaurantId(Long restaurantId);


    List<LandingPage> findAllByRestaurant_RestaurantIdAndActiveTrue(Long restaurantId);

}
