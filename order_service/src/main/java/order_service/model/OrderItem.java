package order_service.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    private Long menuItemId;

    private int quantity;

    private String specialInstructions;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderItem", orphanRemoval = true)
    @JsonManagedReference(value = "orderItem-event")
    private List<OrderEvent> eventList;


}
