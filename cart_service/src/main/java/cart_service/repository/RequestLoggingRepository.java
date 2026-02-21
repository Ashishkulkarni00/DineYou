package cart_service.repository;

import cart_service.model.RequestLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLoggingRepository extends JpaRepository<RequestLogs, Long> {
}
