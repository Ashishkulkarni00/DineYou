package menucard_service.repository;

import menucard_service.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query(value = "SELECT * FROM menu_item mi WHERE mi.restaurant_id = :restaurantId AND available = 1 AND mi.is_popular = 1", nativeQuery = true)
    List<MenuItem> findPopularItemsForRestaurant(Long restaurantId);

    @Query("SELECT mi FROM MenuItem mi WHERE LOWER(mi.itemName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<MenuItem> findBySearchedQuery(@Param("query") String query);

}
