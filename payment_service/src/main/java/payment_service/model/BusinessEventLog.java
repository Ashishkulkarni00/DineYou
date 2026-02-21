package payment_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;

    private String spanId;

    private String eventName;

    private String interactionType;

    private String status;

    private String errorMessage;

    private String timestamp;

}