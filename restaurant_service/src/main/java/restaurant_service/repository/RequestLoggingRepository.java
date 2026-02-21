package restaurant_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurant_service.model.RequestLogs;

@Repository
public interface RequestLoggingRepository extends JpaRepository<RequestLogs, Long> {

}
