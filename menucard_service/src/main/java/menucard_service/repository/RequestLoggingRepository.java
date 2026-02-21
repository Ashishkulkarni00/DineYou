package menucard_service.repository;

import menucard_service.model.RequestLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLoggingRepository extends JpaRepository<RequestLogs, Long> {
}
