package payment_service.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import payment_service.enums.PaymentStatus;
import payment_service.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.status = :status, p.updatedAt = :datetime WHERE p.paymentReferenceId = :referenceId")
    int updateStatusByReferenceId(String referenceId, PaymentStatus status, String datetime);

    Optional<Payment> findByPaymentReferenceId(String paymentReferenceId);


    List<Payment> findByOrderIdIn(List<Long> orderIds);
}
