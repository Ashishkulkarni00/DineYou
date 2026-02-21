package restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant_service.model.BusinessEventLog;

public interface BusinessEventRepository extends JpaRepository<BusinessEventLog, Long> {
}
