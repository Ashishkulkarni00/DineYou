package menucard_service.repository;
import menucard_service.model.Menucard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenucardRepository extends JpaRepository<Menucard, Long> {

    Optional<Menucard> findByRestaurantId(Long restaurantId);

}
