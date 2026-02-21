package menucard_service.repository;

import menucard_service.model.BusinessEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessEventRepository extends JpaRepository<BusinessEventLog, Long> {
}
