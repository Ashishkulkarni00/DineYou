package order_service.repository;

import order_service.model.BusinessEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessEventRepository extends JpaRepository<BusinessEventLog, Long> {
}


