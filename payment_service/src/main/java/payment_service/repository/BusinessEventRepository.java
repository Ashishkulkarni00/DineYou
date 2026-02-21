package payment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payment_service.model.BusinessEventLog;

public interface BusinessEventRepository extends JpaRepository<BusinessEventLog, Long> {
}
