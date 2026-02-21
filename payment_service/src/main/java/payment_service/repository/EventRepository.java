package payment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payment_service.model.PaymentEvents;

public interface EventRepository extends JpaRepository<PaymentEvents, String> {
}
