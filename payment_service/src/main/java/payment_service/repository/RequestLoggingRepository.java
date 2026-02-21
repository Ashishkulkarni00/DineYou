package payment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payment_service.model.RequestLogs;

public interface RequestLoggingRepository extends JpaRepository<RequestLogs, Long> {
}
