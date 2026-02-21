package payment_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvents {

    @Id
    private String eventId;

    private String eventType;

    private String paymentReferenceId;

    @Lob
    @Column(name = "data", columnDefinition = "TEXT")
    private String data;

    @Column(name = "received_at", nullable = false)
    private String receivedAt;

}
