package order_service.repository;

import order_service.model.RequestLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLoggingRepository extends JpaRepository<RequestLogs, Long> {

}

