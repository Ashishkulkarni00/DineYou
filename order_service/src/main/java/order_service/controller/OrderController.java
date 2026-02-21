package order_service.controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import order_service.dto.APIResponse;
import order_service.dto.PlaceOrderReqDto;
import order_service.dto.UpdateOrderEventReqDto;
import order_service.dto.order_status_response.OrderWithStatusGroupsDto;
import order_service.model.Order;
import order_service.model.OrderEvent;
import order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/order")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<APIResponse<Order>> placeOrder(
            @RequestBody PlaceOrderReqDto placeOrderReqDto,
            HttpServletRequest request
            ){
        String requestId = (String) request.getAttribute("requestId");
        return orderService.placeOrder(placeOrderReqDto, requestId);
    }

    @PutMapping("/updateOrderEvent")
    public ResponseEntity<APIResponse<OrderEvent>> updateOrderEvent(
            @RequestBody @Valid UpdateOrderEventReqDto updateOrderEventReqDto,
            HttpServletRequest request) {
        String requestId = (String) request.getAttribute("requestId");
        return orderService.updateOrderItemEvent(updateOrderEventReqDto.getOrderId(), updateOrderEventReqDto.getOrderItemId(), updateOrderEventReqDto.getEventType(), requestId);
    }

//    @GetMapping("/getOrderStatus/{orderId}")
//    public ResponseEntity<APIResponse<OrderStatusResDto>> getOrderStatus(
//            @PathVariable("orderId") Long orderId,
//            HttpServletRequest request
//            ){
//        String requestId = (String) request.getAttribute("requestId");
//        return orderService.getOrderStatus(orderId, requestId);
//    }

    @GetMapping("/getOrders/{userId}")
    public ResponseEntity<APIResponse<List<OrderWithStatusGroupsDto>>> getActiveOrdersByUser(
            @PathVariable("userId") String userId,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return orderService.getActiveOrdersByUserV2(userId,requestId);
    }

    //Helper methods
    @GetMapping("/exists")
    public ResponseEntity<APIResponse<Map<Long, Boolean>>> checkOrderExistence
    (@RequestParam("orderIds") List<Long> orderIds, HttpServletRequest request){
        String requestId = (String) request.getAttribute("requestId");
        return orderService.checkExistence(requestId, orderIds);
    }

}
