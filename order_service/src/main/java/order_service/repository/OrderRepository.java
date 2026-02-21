package order_service.repository;

import order_service.enums.OrderStatus;
import order_service.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {


    @EntityGraph(attributePaths = {})
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.orderStatus IN :activeStatuses ORDER BY o.orderStatus")
    List<Order> findActiveOrdersByUser(@Param("userId") String userId,
                                       @Param("activeStatuses") List<OrderStatus> activeStatuses);


    @Query("SELECT o.orderId FROM Order o WHERE o.orderId IN :orderIds")
    List<Long> findExistingOrderIds(@Param("orderIds") List<Long> orderIds);
}
