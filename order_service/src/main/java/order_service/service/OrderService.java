package order_service.service;

import com.google.gson.JsonObject;
import order_service.dto.*;
import order_service.dto.get_orders.MenuItemDto;
import order_service.dto.get_orders.OrderWithItemsDto;
import order_service.dto.order_status_response.OrderWithStatusGroupsDto;
import order_service.enums.ErrorCode;
import order_service.enums.OrderEventType;
import order_service.enums.OrderStatus;
import order_service.exception.ApplicationException;
import order_service.model.BusinessEventLog;
import order_service.model.Order;
import order_service.model.OrderEvent;
import order_service.model.OrderItem;
import order_service.repository.OrderEventRepository;
import order_service.repository.OrderItemRepository;
import order_service.repository.OrderRepository;
import order_service.util.DateTimeUtil;
import order_service.util.RequestInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {

     @Autowired
     private OrderRepository orderRepository;

     @Autowired
     private DateTimeUtil dateTimeUtil;

     @Autowired
     private RestaurantServiceClient restaurantServiceClient;

     @Autowired
     private CartServiceClient cartServiceClient;

     @Autowired
     private OrderEventRepository eventRepository;

     @Autowired
     private OrderItemRepository orderItemRepository;

     @Autowired
     private MenucardServiceClient menucardServiceClient;

     @Autowired
     private BusinessEventLoggingService businessEventLoggingService;

     @Autowired
     private RequestInfoProvider requestInfoProvider;

     public ResponseEntity<APIResponse<Order>> placeOrder(PlaceOrderReqDto orderReqDto, String requestId) {

          String timestamp = dateTimeUtil.getDateTime();
          String spanId = UUID.randomUUID().toString();

          try {

               if (!restaurantServiceClient.doesRestaurantExist(orderReqDto.getRestaurantId(), requestId)) {

                    BusinessEventLog eventLog = BusinessEventLog.builder()
                            .requestId(requestId)
                            .spanId(spanId)
                            .eventName("PLACE_ORDER")
                            .interactionType("API_CALL")
                            .status("BUSINESS_FAILURE")
                            .errorMessage("Invalid restaurant Id provided")
                            .timestamp(timestamp)
                            .build();
                    businessEventLoggingService.saveBusinessEvent(eventLog);

                    throw new ApplicationException(
                              ErrorCode.RESOURCE_NOT_FOUND,
                              "Invalid restaurant Id provided",
                              "restaurantId");
               }

               ValidateCartAndMenuItemsReqDto reqDto = new ValidateCartAndMenuItemsReqDto();

               reqDto.setCartId(orderReqDto.getCartId());
               List<Long> menuItemIds = new ArrayList<>();
               for (OrderItemDto orderItemDto : orderReqDto.getOrderItems()) {
                    menuItemIds.add(orderItemDto.getMenuItemId());
               }
               reqDto.setMenuItemIds(menuItemIds);

               if (!cartServiceClient.validateCartAndMenuItems(reqDto, requestId)) {

                    BusinessEventLog eventLog = BusinessEventLog.builder()
                            .requestId(requestId)
                            .spanId(spanId)
                            .eventName("PLACE_ORDER")
                            .interactionType("API_CALL")
                            .status("BUSINESS_FAILURE")
                            .errorMessage("Invalid menu item ID's provided")
                            .timestamp(timestamp)
                            .build();

                    businessEventLoggingService.saveBusinessEvent(eventLog);

                    throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid menu item ID's provided",
                              "menuItemIds");
               }

               Order order = new Order();
               order.setCreatedAt(timestamp);
               order.setUpdatedAt(timestamp);
               order.setUserId(orderReqDto.getUserId());
               order.setRestaurantId(orderReqDto.getRestaurantId());
               order.setCartId(orderReqDto.getCartId());
               order.setKeycloakSessionId(requestInfoProvider.getKeycloakSessionId());
               order.setOrderStatus(OrderStatus.PLACED);

               List<OrderItem> orderItems = new ArrayList<>();

               for (OrderItemDto itemDto : orderReqDto.getOrderItems()) {

                    OrderItem orderItem = new OrderItem();
                    orderItem.setMenuItemId(itemDto.getMenuItemId());
                    orderItem.setQuantity(itemDto.getQuantity());
                    orderItem.setSpecialInstructions(itemDto.getSpecialInstructions());
                    orderItem.setOrder(order);

                    // Create initial order event
                    OrderEvent orderEvent = new OrderEvent();
                    orderEvent.setOrderItem(orderItem);
                    orderEvent.setEventType(OrderEventType.ORDER_PLACED);
                    orderEvent.setTimestamp(timestamp);
                    orderEvent.setOrder(order);
                    orderEvent.setPerformedBy("SYSTEM"); // Or replace with actual user info

                    // Add event to order item
                    orderItem.setEventList(List.of(orderEvent));

                    // Add to order's item list
                    orderItems.add(orderItem);
               }

               // Attach items to order
               order.setOrderItemList(orderItems);

               // Save order
               Order savedOrder = orderRepository.save(order);

               // UPDATING CART
               JsonObject jsonObject = cartServiceClient.updateCartStatus("ORDERED", order.getCartId(), requestId);

               BusinessEventLog eventLog = BusinessEventLog.builder()
                       .requestId(requestId)
                       .spanId(spanId)
                       .eventName("PLACE_ORDER")
                       .interactionType("API_CALL")
                       .status("BUSINESS_SUCCESS")
                       .timestamp(timestamp)
                       .build();
               businessEventLoggingService.saveBusinessEvent(eventLog);

               // Build and return response
               return ResponseEntity.ok(
                         APIResponse.<Order>builder()
                                   .success(true)
                                   .message("Order placed successfully")
                                   .data(savedOrder)
                                   .timestamp(timestamp)
                                   .requestId(requestId)
                                   .build());

          } catch (ApplicationException e) {
               BusinessEventLog eventLog = BusinessEventLog.builder()
                       .requestId(requestId)
                       .spanId(spanId)
                       .eventName("PLACE_ORDER")
                       .interactionType("API_CALL")
                       .status("BUSINESS_FAILURE")
                       .errorMessage(e.getMessage())
                       .timestamp(timestamp)
                       .build();
                       
               businessEventLoggingService.saveBusinessEvent(eventLog);
               return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
          } catch (Exception e) {
               BusinessEventLog eventLog = BusinessEventLog.builder()
                       .requestId(requestId)
                       .spanId(spanId)
                       .eventName("PLACE_ORDER")
                       .interactionType("API_CALL")
                       .status("BUSINESS_FAILURE")
                       .errorMessage(e.getMessage())
                       .timestamp(timestamp)
                       .build();
               businessEventLoggingService.saveBusinessEvent(eventLog);
               return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp,
                         HttpStatus.INTERNAL_SERVER_ERROR);
          }
     }

     public ResponseEntity<APIResponse<OrderEvent>> updateOrderItemEvent(Long orderId, Long orderItemId,
               String eventType, String requestId) {

          String timestamp = dateTimeUtil.getDateTime();

          try {
               OrderEventType orderEventType;
               try {
                    orderEventType = OrderEventType.valueOf(eventType.toUpperCase());
               } catch (IllegalArgumentException e) {
                    throw new ApplicationException(
                              ErrorCode.BAD_REQUEST,
                              "Invalid event type provided. Allowed values: "
                                        + Arrays.toString(OrderEventType.values()),
                              "orderEventType");
               }

               Optional<Order> existingOrder = orderRepository.findById(orderId);
               if (!existingOrder.isPresent()) {
                    throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid order ID provided",
                              "orderId");
               }
               Optional<OrderItem> existingOrderItem = orderItemRepository.findById(orderItemId);
               if (!existingOrderItem.isPresent()) {
                    throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid order item ID provided",
                              "orderItemId");
               }

               OrderEvent orderEvent = new OrderEvent();
               orderEvent.setOrderItem(existingOrderItem.get());
               orderEvent.setOrder(existingOrder.get());
               orderEvent.setEventType(orderEventType);
               orderEvent.setPerformedBy("SYSTEM FOR NOW");
               orderEvent.setTimestamp(timestamp);

               orderEvent = eventRepository.save(orderEvent);

               return APIResponse.success(orderEvent, "Order event updated successfully", requestId, timestamp,
                         HttpStatus.OK);

          } catch (ApplicationException e) {
               return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.BAD_REQUEST);
          } catch (Exception e) {
               return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, "Unexpected error occurred", requestId,
                         timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
          }
     }


     public ResponseEntity<APIResponse<Map<Long, Boolean>>> checkExistence(
               String requestId,
               List<Long> orderIds) {
          String timestamp = dateTimeUtil.getDateTime();
          String spanId = UUID.randomUUID().toString();
          try {
               // Fetch only existing order IDs in ONE DB hit
               List<Long> existingIds = orderRepository.findExistingOrderIds(orderIds);

               Map<Long, Boolean> resultMap = new HashMap<>();

               for (Long id : orderIds) {
                    resultMap.put(id, existingIds.contains(id));
               }

               BusinessEventLog eventLog = BusinessEventLog.builder()
                       .requestId(requestId)
                       .spanId(spanId)
                       .eventName("CHECK_ORDER_EXISTENCE")
                       .interactionType("API_CALL")
                       .status("BUSINESS_SUCCESS")
                       .timestamp(timestamp)
                       .build();
               businessEventLoggingService.saveBusinessEvent(eventLog);
               return APIResponse.success(
                         resultMap,
                         "Order existence check completed",
                         requestId,
                         timestamp,
                         HttpStatus.OK);

          } catch (ApplicationException e) {
               e.printStackTrace();

               BusinessEventLog eventLog = BusinessEventLog.builder()
                       .requestId(requestId)
                       .spanId(spanId)
                       .eventName("CHECK_ORDER_EXISTENCE")
                       .interactionType("API_CALL")
                       .status("BUSINESS_FAILURE")
                       .errorMessage(e.getMessage())
                       .timestamp(timestamp)
                       .build();
               businessEventLoggingService.saveBusinessEvent(eventLog);

               return APIResponse.error(
                         e.getErrorCode(),
                         e.getMessage(),
                         requestId,
                         timestamp,
                         HttpStatus.NOT_FOUND);

          } catch (Exception e) {
               e.printStackTrace();
               BusinessEventLog eventLog = BusinessEventLog.builder()
                       .requestId(requestId)
                       .spanId(spanId)
                       .eventName("CHECK_ORDER_EXISTENCE")
                       .interactionType("API_CALL")
                       .status("BUSINESS_FAILURE")
                       .errorMessage(e.getMessage())
                       .timestamp(timestamp)
                       .build();
               businessEventLoggingService.saveBusinessEvent(eventLog);
               return APIResponse.error(
                         ErrorCode.INTERNAL_SERVER_ERROR,
                         e.getMessage(),
                         requestId,
                         timestamp,
                         HttpStatus.INTERNAL_SERVER_ERROR);
          }
     }

     public ResponseEntity<APIResponse<Map<String, List<OrderWithItemsDto>>>> getActiveOrdersByUserV1(String userId,
               String requestId) {
          String timestamp = dateTimeUtil.getDateTime();

          try {
               List<OrderStatus> activeStatuses = List.of(
                       OrderStatus.PLACED,
                       OrderStatus.PREPARING,
                       OrderStatus.READY,
                       OrderStatus.PARTIALLY_SERVED,
                       OrderStatus.SERVED,
                       OrderStatus.CANCELLATION_REQUESTED,
                       OrderStatus.PAYMENT_PENDING,
                       OrderStatus.PAID // if user pays the bill while order is active we will consider the order as active
               );

               List<Order> orders = orderRepository.findActiveOrdersByUser(userId, activeStatuses);

               if (orders.isEmpty()) {
                    throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Orders not available", "order");
               }

               // Collect menuItemIds
               List<Long> menuItemIds = orders.stream()
                         .flatMap(o -> o.getOrderItemList().stream())
                         .map(OrderItem::getMenuItemId)
                         .distinct()
                         .toList();

               List<MenuItemResDto> menuItemResDtoList = menucardServiceClient.getAllMenuItems(menuItemIds, requestId);
               Map<Long, MenuItemResDto> menuItemMap = menuItemResDtoList.stream()
                         .collect(Collectors.toMap(MenuItemResDto::getItemId, Function.identity()));

               // Map to hold status -> list of orders
               Map<String, List<OrderWithItemsDto>> groupedOrders = new HashMap<>();

               for (Order order : orders) {

                    // Group items by last event
                    Map<String, List<order_service.dto.get_orders.OrderItemDto>> itemsByStatus = new HashMap<>();

                    for (OrderItem orderItem : order.getOrderItemList()) {
                         String status = "UNKNOWN";
                         if (orderItem.getEventList() != null && !orderItem.getEventList().isEmpty()) {
                              status = orderItem.getEventList().get(orderItem.getEventList().size() - 1).getEventType()
                                        .name();
                         }

                         MenuItemResDto menuItemRes = menuItemMap.get(orderItem.getMenuItemId());

                         order_service.dto.get_orders.OrderItemDto orderItemDto = new order_service.dto.get_orders.OrderItemDto();
                         orderItemDto.setOrderItemId(orderItem.getOrderItemId());
                         orderItemDto.setOrderId(order.getOrderId());
                         orderItemDto.setMenuItemId(orderItem.getMenuItemId());
                         orderItemDto.setQuantity(orderItem.getQuantity());
                         orderItemDto.setSpecialInstructions(orderItem.getSpecialInstructions());

                         MenuItemDto menuItemDto = new MenuItemDto();
                         menuItemDto.setItemName(menuItemRes.getItemName());
                         menuItemDto.setItemPrice(menuItemRes.getItemPrice());
                         menuItemDto.setCategoryName(null);
                         menuItemDto.setImagePath(menuItemRes.getImagePath());

                         orderItemDto.setMenuItem(menuItemDto);

                         itemsByStatus.computeIfAbsent(status, k -> new ArrayList<>()).add(orderItemDto);
                    }

                    // Add order items into groupedOrders map
                    itemsByStatus.forEach((status, itemList) -> {
                         OrderWithItemsDto orderWithItems = new OrderWithItemsDto();
                         orderWithItems.setOrderId(order.getOrderId());
                         orderWithItems.setOrderItems(itemList);
                         groupedOrders.computeIfAbsent(status, k -> new ArrayList<>()).add(orderWithItems);
                    });
               }

               return APIResponse.success(groupedOrders, "Orders fetched successfully", requestId, timestamp,
                         HttpStatus.OK);

          } catch (ApplicationException e) {
               return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
          } catch (Exception e) {
               return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp,
                         HttpStatus.INTERNAL_SERVER_ERROR);
          }
     }


     public ResponseEntity<APIResponse<List<OrderWithStatusGroupsDto>>> getActiveOrdersByUserV2(
             String userId,
             String requestId
     ) {
          String timestamp = dateTimeUtil.getDateTime();
          String spanId = UUID.randomUUID().toString();

          try {
               List<OrderStatus> activeStatuses = List.of(
                       OrderStatus.PLACED,
                       OrderStatus.PREPARING,
                       OrderStatus.READY,
                       OrderStatus.PARTIALLY_SERVED,
                       OrderStatus.SERVED,
                       OrderStatus.CANCELLATION_REQUESTED,
                       OrderStatus.PAYMENT_PENDING,
                       OrderStatus.PAID // if user pays the bill while order is active we will consider the order as active
               );

               List<Order> orders = orderRepository.findActiveOrdersByUser(userId, activeStatuses);

               if (orders.isEmpty()) {

                    BusinessEventLog eventLog = BusinessEventLog.builder()
                            .requestId(requestId)
                            .spanId(spanId)
                            .eventName("FETCH_ACTIVE_ORDERS")
                            .interactionType("API_CALL")
                            .status("BUSINESS_FAILURE")
                            .errorMessage("No active orders found for the user")
                            .timestamp(timestamp)
                            .build();
                    businessEventLoggingService.saveBusinessEvent(eventLog);

                    throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                            "No active orders found for the user", "order");
               }

               // Collect all menuItemIds
               List<Long> menuItemIds = orders.stream()
                       .flatMap(o -> o.getOrderItemList().stream())
                       .map(OrderItem::getMenuItemId)
                       .distinct()
                       .toList();

               List<MenuItemResDto> menuItemResDtoList =
                       menucardServiceClient.getAllMenuItems(menuItemIds, requestId);

               Map<Long, MenuItemResDto> menuItemMap = menuItemResDtoList.stream()
                       .collect(Collectors.toMap(MenuItemResDto::getItemId, Function.identity()));

               // Final response list
               List<OrderWithStatusGroupsDto> responseList = new ArrayList<>();

               for (Order order : orders) {

                    // Step 1: Group items by latest event status
                    Map<String, List<order_service.dto.get_orders.OrderItemDto>> statusGroups = new HashMap<>();

                    for (OrderItem orderItem : order.getOrderItemList()) {

                         String status = "UNKNOWN";
                         if (orderItem.getEventList() != null && !orderItem.getEventList().isEmpty()) {
                              status = orderItem.getEventList()
                                      .get(orderItem.getEventList().size() - 1)
                                      .getEventType()
                                      .name();
                         }

                         MenuItemResDto menuItemRes = menuItemMap.get(orderItem.getMenuItemId());

                         // Build item DTO
                         order_service.dto.get_orders.OrderItemDto itemDto = new order_service.dto.get_orders.OrderItemDto();
                         itemDto.setOrderItemId(orderItem.getOrderItemId());
                         itemDto.setMenuItemId(orderItem.getMenuItemId());
                         itemDto.setOrderId(order.getOrderId());
                         itemDto.setQuantity(orderItem.getQuantity());
                         itemDto.setSpecialInstructions(orderItem.getSpecialInstructions());

                         MenuItemDto menuItemDto = new MenuItemDto();
                         menuItemDto.setItemName(menuItemRes.getItemName());
                         menuItemDto.setItemPrice(menuItemRes.getItemPrice());
                         menuItemDto.setImagePath(menuItemRes.getImagePath());
                         menuItemDto.setCategoryName(null);

                         itemDto.setMenuItem(menuItemDto);

                         statusGroups.computeIfAbsent(status, k -> new ArrayList<>()).add(itemDto);
                    }

                    // Step 2: Build final order dto containing statusGroups
                    OrderWithStatusGroupsDto dto = new OrderWithStatusGroupsDto();
                    dto.setOrderId(order.getOrderId());
//                    dto.setTableId(order.getTableId());
                    dto.setCreatedAt(order.getCreatedAt());
                    dto.setUpdatedAt(order.getUpdatedAt());
                    dto.setStatusGroups(statusGroups);

                    responseList.add(dto);
               }

               BusinessEventLog eventLog = BusinessEventLog.builder()
                       .requestId(requestId)
                       .spanId(spanId)
                       .eventName("FETCH_ACTIVE_ORDERS")
                       .interactionType("API_CALL")
                       .status("BUSINESS_SUCCESS")
                       .timestamp(timestamp)
                       .build();
               businessEventLoggingService.saveBusinessEvent(eventLog);

               return APIResponse.success(
                       responseList,
                       "Orders fetched successfully",
                       requestId,
                       timestamp,
                       HttpStatus.OK
               );

          } catch (ApplicationException e) {

               BusinessEventLog eventLog = BusinessEventLog.builder()
                       .requestId(requestId)
                       .spanId(spanId)
                       .eventName("FETCH_ACTIVE_ORDERS")
                       .interactionType("API_CALL")
                       .status("BUSINESS_FAILURE")
                       .errorMessage(e.getMessage())
                       .timestamp(timestamp)
                       .build();
               businessEventLoggingService.saveBusinessEvent(eventLog);

               return APIResponse.error(
                       e.getErrorCode(),
                       e.getMessage(),
                       requestId,
                       timestamp,
                       HttpStatus.NOT_FOUND
               );
          } catch (Exception e) {

               e.printStackTrace();

               BusinessEventLog eventLog = BusinessEventLog.builder()
                       .requestId(requestId)
                       .spanId(spanId)
                       .eventName("FETCH_ACTIVE_ORDERS")
                       .interactionType("API_CALL")
                       .status("BUSINESS_FAILURE")
                       .errorMessage(e.getMessage())
                       .timestamp(timestamp)
                       .build();
               businessEventLoggingService.saveBusinessEvent(eventLog);

               return APIResponse.error(
                       ErrorCode.INTERNAL_SERVER_ERROR,
                       e.getMessage(),
                       requestId,
                       timestamp,
                       HttpStatus.INTERNAL_SERVER_ERROR
               );
          }
     }


}
