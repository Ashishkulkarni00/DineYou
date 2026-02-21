package order_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import order_service.enums.OrderEventType;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne
    @JsonBackReference(value = "orderItem-event")
    @JoinColumn(name = "orderItemId")
    private OrderItem orderItem;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "orderId")
    private Order order;

    @Enumerated
    private OrderEventType eventType;

    private String timestamp;

    private String performedBy;

}
