package cart_service.repository;

import cart_service.model.BusinessEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessEventRepository extends JpaRepository<BusinessEventLog, Long> {
}

