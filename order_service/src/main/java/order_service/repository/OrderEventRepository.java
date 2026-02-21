package order_service.repository;

import order_service.model.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {

    @Query("""
                SELECT e FROM OrderEvent e
                WHERE e.orderItem.orderItemId IN :orderItemIds
                AND e.timestamp = (
                    SELECT MAX(ev.timestamp)
                    FROM OrderEvent ev
                    WHERE ev.orderItem.orderItemId = e.orderItem.orderItemId
                )
            """)
    List<OrderEvent> findLatestEventsByOrderItemIds(@Param("orderItemIds") List<Long> orderItemIds);


}
