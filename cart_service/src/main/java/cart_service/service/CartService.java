package cart_service.service;

import cart_service.dto.*;
import cart_service.enums.CartItemStatus;
import cart_service.enums.CartStatus;
import cart_service.enums.ErrorCode;
import cart_service.exception.ApplicationException;
import cart_service.model.BusinessEventLog;
import cart_service.model.Cart;
import cart_service.model.CartItem;
import cart_service.repository.CartItemRepository;
import cart_service.repository.CartRepository;
import cart_service.util.DateTimeUtil;
import cart_service.util.RequestInfoProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class CartService {

        @Autowired
        private CartRepository cartRepository;

        @Autowired
        private DateTimeUtil dateTimeUtil;

        @Autowired
        private RestaurantServiceClient restaurantServiceClient;

        @Autowired
        private CartItemRepository cartItemRepository;

        @Autowired
        private ModelMapper modelMapper;

        @Autowired
        private CartItemService cartItemService;

        @Autowired
        private MenucardServiceClient menucardServiceClient;

        @Autowired
        private RequestInfoProvider requestInfoProvider;

        @Autowired
        private BusinessEventLoggingService businessEventLoggingService;

        public ResponseEntity<APIResponse<Cart>> createCart(CreateCartReqDto createCartReqDto, String requestId) {

                String timestamp = dateTimeUtil.getDateTime();
                String spanId = UUID.randomUUID().toString();

                try {

                        if (!restaurantServiceClient.doesRestaurantExist(createCartReqDto.getRestaurantId(),
                                        requestId)) {

                                BusinessEventLog eventLog = BusinessEventLog.builder()
                                                .requestId(requestId)
                                                .spanId(spanId)
                                                .eventName("CREATE_CART")
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

                        Optional<Cart> existingCart = cartRepository.findByUserIdAndRestaurantIdAndStatus(
                                        createCartReqDto.getUserId(), createCartReqDto.getRestaurantId(),
                                        CartStatus.ACTIVE);

                        if (existingCart.isPresent() && existingCart.get().getCartStatus() == CartStatus.ACTIVE) {

                                BusinessEventLog eventLog = BusinessEventLog.builder()
                                                .requestId(requestId)
                                                .spanId(spanId)
                                                .eventName("CREATE_CART_RETURNING_SAME_CART_AS_ALREADY_EXISTED")
                                                .interactionType("API_CALL")
                                                .status("BUSINESS_SUCCESS")
                                                .timestamp(timestamp)
                                                .build();
                                businessEventLoggingService.saveBusinessEvent(eventLog);
                                return APIResponse.success(existingCart.get(), "Cart already exists returning the same",
                                                requestId,
                                                timestamp, HttpStatus.OK);
                        }

                        Cart cart = new Cart();
                        cart.setUserId(createCartReqDto.getUserId());
                        cart.setAnonymousSessionId(requestInfoProvider.getAnonymousSessionId());
                        cart.setKeycloakSessionId(requestInfoProvider.getKeycloakSessionId());
                        cart.setRequestId(requestId);
                        cart.setRestaurantId(createCartReqDto.getRestaurantId());
                        cart.setCartStatus(CartStatus.ACTIVE);
                        cart.setCreatedAt(dateTimeUtil.getDateTime());
                        cart.setUpdatedAt(dateTimeUtil.getDateTime());
                        cart = cartRepository.save(cart);

                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("CREATE_CART")
                                        .interactionType("API_CALL")
                                        .status("BUSINESS_SUCCESS")
                                        .timestamp(timestamp)
                                        .build();
                        businessEventLoggingService.saveBusinessEvent(eventLog);

                        return APIResponse.success(cart, "Cart created successfully", requestId, timestamp,
                                        HttpStatus.CREATED);

                } catch (ApplicationException e) {
                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("CREATE_CART")
                                        .interactionType("API_CALL")
                                        .status("BUSINESS_FAILURE")
                                        .errorMessage(e.getMessage())
                                        .timestamp(timestamp)
                                        .build();
                        businessEventLoggingService.saveBusinessEvent(eventLog);
                        return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp,
                                        HttpStatus.NOT_FOUND);
                } catch (Exception e) {
                        e.printStackTrace();
                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("CREATE_CART")
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

        public ResponseEntity<APIResponse<Cart>> getCartWithCartItems(String userId, String requestId) {

                String timestamp = dateTimeUtil.getDateTime();
                String spanId = UUID.randomUUID().toString();
                try {
                        // This checks whether user has cart already or not - DB CALL
                        Optional<Cart> existingCart = cartRepository.findCartByUserId(userId, CartStatus.ACTIVE);
                        if (!existingCart.isPresent()) {

                                BusinessEventLog eventLog = BusinessEventLog.builder()
                                                .requestId(requestId)
                                                .spanId(spanId)
                                                .eventName("FETCH_CART_ITEMS")
                                                .interactionType("API_CALL")
                                                .status("BUSINESS_FAILURE")
                                                .errorMessage("Cart not found, may be new session")
                                                .timestamp(timestamp)
                                                .build();
                                businessEventLoggingService.saveBusinessEvent(eventLog);

                                throw new ApplicationException(
                                                ErrorCode.RESOURCE_NOT_FOUND,
                                                "Cart not found, may be new session",
                                                "cartId");
                        } else {

                                existingCart.get().getCartItemList()
                                                .removeIf(item -> item.getItemStatus() == CartItemStatus.REMOVED);

                                BusinessEventLog eventLog = BusinessEventLog.builder()
                                                .requestId(requestId)
                                                .spanId(spanId)
                                                .eventName("FETCH_CART_ITEMS")
                                                .interactionType("API_CALL")
                                                .status("BUSINESS_SUCCESS")
                                                .timestamp(timestamp)
                                                .build();
                                businessEventLoggingService.saveBusinessEvent(eventLog);

                                return APIResponse.success(existingCart.get(), "Cart fetched successfully", requestId,
                                                timestamp,
                                                HttpStatus.OK);
                        }
                } catch (ApplicationException e) {
                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("FETCH_CART_ITEMS")
                                        .interactionType("API_CALL")
                                        .status("BUSINESS_FAILURE")
                                        .errorMessage(e.getMessage())
                                        .timestamp(timestamp)
                                        .build();
                        businessEventLoggingService.saveBusinessEvent(eventLog);
                        return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp,
                                        HttpStatus.NOT_FOUND);
                } catch (Exception e) {
                        e.printStackTrace();
                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("FETCH_CART_ITEMS")
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

        public ResponseEntity<APIResponse<Void>> deleteCart(Long cartId, String requestId) {
                String timestamp = dateTimeUtil.getDateTime();
                try {
                        if (cartRepository.existsById(cartId)) {
                                Optional<Cart> existingCart = cartRepository.findById(cartId);
                                if (existingCart.get().getCartItemList() != null
                                                && !existingCart.get().getCartItemList().isEmpty()) {
                                        throw new ApplicationException(ErrorCode.RESOURCE_DEPENDENCY_CONFLICT,
                                                        "Items present in cart.  ",
                                                        "cartId");
                                }
                                return APIResponse.success(null, "Cart deleted successfully", requestId, timestamp,
                                                HttpStatus.OK);
                        } else {
                                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid cart id",
                                                "cartId");
                        }
                } catch (ApplicationException e) {
                        return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp,
                                        HttpStatus.NOT_FOUND);
                } catch (Exception e) {
                        return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp,
                                        HttpStatus.INTERNAL_SERVER_ERROR);
                }
        }

        public ResponseEntity<APIResponse<CartItem>> updateCart(UpdateCartReqDto updateCartReqDto, String requestId) {

                String timestamp = dateTimeUtil.getDateTime();

                try {
                        CartItem cartItem = cartItemRepository.findById(updateCartReqDto.getCartItemId())
                                        .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                                                        "Invalid cart item ID provided", "cartItemId"));

                        if (updateCartReqDto.getQuantity() == 0) {
                                Cart cart = cartItem.getCart();
                                cart.getCartItemList().remove(cartItem);
                                cartRepository.save(cart);
                                return APIResponse.success(null, "Cart item removed successfully", requestId, timestamp,
                                                HttpStatus.OK);
                        }

                        modelMapper.map(updateCartReqDto, cartItem);
                        cartItem.setUpdatedAt(timestamp);
                        cartItem = cartItemRepository.save(cartItem);

                        return APIResponse.success(cartItem, "Cart item updated successfully", requestId, timestamp,
                                        HttpStatus.OK);

                } catch (ApplicationException e) {
                        return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp,
                                        HttpStatus.NOT_FOUND);
                } catch (Exception e) {
                        return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp,
                                        HttpStatus.INTERNAL_SERVER_ERROR);
                }
        }

        public ResponseEntity<APIResponse<Map<String, Double>>> calculateCartTotal(Long cartId, String requestId) {

                String timestamp = dateTimeUtil.getDateTime();
                String spanId = UUID.randomUUID().toString();

                try {

                        Cart cart = cartRepository.findById(cartId)
                                        .orElseThrow(new Supplier<ApplicationException>() {
                                                @Override
                                                public ApplicationException get() {

                                                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                                                        .requestId(requestId)
                                                                        .spanId(spanId)
                                                                        .eventName("CALCULATE_CART_TOTAL")
                                                                        .interactionType("API_CALL")
                                                                        .status("BUSINESS_FAILURE")
                                                                        .errorMessage("Invalid cart ID provided")
                                                                        .timestamp(timestamp)
                                                                        .build();
                                                        businessEventLoggingService.saveBusinessEvent(eventLog);

                                                        return new ApplicationException(
                                                                        ErrorCode.RESOURCE_NOT_FOUND,
                                                                        "Invalid cart ID provided",
                                                                        "cartId");
                                                }
                                        });

                        List<CartItem> filteredCartItems = cart.getCartItemList().stream()
                                        .filter(item -> item.getItemStatus() != CartItemStatus.REMOVED &&
                                                        item.getItemStatus() != CartItemStatus.CANCELLED)
                                        .toList();

                        Map<Long, Integer> itemQuantityMap = new HashMap<>();

                        List<Long> menuItemIds = filteredCartItems
                                        .stream()
                                        .peek(item -> itemQuantityMap.put(item.getMenuItemId(), item.getQuantity()))
                                        .map(CartItem::getMenuItemId)
                                        .toList();

                        if (menuItemIds.isEmpty()) {
                                BusinessEventLog eventLog = BusinessEventLog.builder()
                                                .requestId(requestId)
                                                .spanId(spanId)
                                                .eventName("CALCULATE_CART_TOTAL")
                                                .interactionType("API_CALL")
                                                .status("BUSINESS_FAILURE")
                                                .errorMessage("No cart items found")
                                                .timestamp(timestamp)
                                                .build();
                                businessEventLoggingService.saveBusinessEvent(eventLog);
                                throw new ApplicationException(ErrorCode.MISSING_REQUIRED_FIELD, "No cart items found",
                                                "cartItems");
                        }

                        List<MenuItemResDto> menuItemResDtoList = menucardServiceClient.getAllMenuItems(menuItemIds,
                                        requestId);

                        Double total = 0.0;
                        for (MenuItemResDto dto : menuItemResDtoList) {
                                total += (dto.getItemPrice() * itemQuantityMap.get(dto.getItemId()));
                        }

                        Map<String, Double> totalPriceMap = new HashMap<>();
                        totalPriceMap.put("totalPrice", total);

                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("CALCULATE_CART_TOTAL")
                                        .interactionType("API_CALL")
                                        .status("BUSINESS_SUCCESS")
                                        .timestamp(timestamp)
                                        .build();

                        businessEventLoggingService.saveBusinessEvent(eventLog);

                        return APIResponse.success(totalPriceMap, "Cart item updated successfully", requestId,
                                        timestamp,
                                        HttpStatus.OK);

                } catch (ApplicationException e) {
                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("CALCULATE_CART_TOTAL")
                                        .interactionType("API_CALL")
                                        .status("BUSINESS_FAILURE")
                                        .errorMessage(e.getMessage())
                                        .timestamp(timestamp)
                                        .build();
                        businessEventLoggingService.saveBusinessEvent(eventLog);
                        return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp,
                                        HttpStatus.NOT_FOUND);
                } catch (Exception e) {
                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("CALCULATE_CART_TOTAL")
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

        public ResponseEntity<APIResponse<Boolean>> validateCartIdAndMenuItemsForPlacingOrder(
                        OrderPlacementReqDto orderPlacementReqDto, String requestId) {

                String timestamp = dateTimeUtil.getDateTime();
                String spanId = UUID.randomUUID().toString();

                try {

                        Cart cart = cartRepository.findById(orderPlacementReqDto.getCartId())
                                        .orElseThrow(new Supplier<ApplicationException>() {
                                                @Override
                                                public ApplicationException get() {

                                                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                                                        .requestId(requestId)
                                                                        .spanId(spanId)
                                                                        .eventName("VALIDATE_CART_AND_MENU_ITEMS_BEFORE_ORDER_PLACEMENT")
                                                                        .interactionType("API_CALL")
                                                                        .status("BUSINESS_FAILURE")
                                                                        .errorMessage("Invalid cart ID provided")
                                                                        .timestamp(timestamp)
                                                                        .build();
                                                        businessEventLoggingService.saveBusinessEvent(eventLog);

                                                        return new ApplicationException(
                                                                        ErrorCode.RESOURCE_NOT_FOUND,
                                                                        "Invalid cart ID provided",
                                                                        "cartId");
                                                }
                                        });

                        Collections.sort(orderPlacementReqDto.getMenuItemIds());

                        List<Long> menuItemIds = cart.getCartItemList().stream()
                                        .filter(item -> item.getItemStatus() == CartItemStatus.ADDED)
                                        .map(CartItem::getMenuItemId)
                                        .collect(Collectors.toList());

                        Collections.sort(menuItemIds);

                       
                        if (menuItemIds.size() != orderPlacementReqDto.getMenuItemIds().size()) {
                                BusinessEventLog eventLog = BusinessEventLog.builder()
                                                .requestId(requestId)
                                                .spanId(spanId)
                                                .eventName("VALIDATE_CART_AND_MENU_ITEMS_BEFORE_ORDER_PLACEMENT")
                                                .interactionType("API_CALL")
                                                .status("BUSINESS_FAILURE")
                                                .errorMessage("All menu item ID's are not present in cart")
                                                .timestamp(timestamp)
                                                .build();
                                businessEventLoggingService.saveBusinessEvent(eventLog);
                                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                                                "All menu item ID's are not present in cart", "menuItemIds");
                        }

                        for (int i = 0; i < menuItemIds.size(); i++) {
                                if (!Objects.equals(menuItemIds.get(i), orderPlacementReqDto.getMenuItemIds().get(i))) {
                                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                                        .requestId(requestId)
                                                        .spanId(spanId)
                                                        .eventName("VALIDATE_CART_AND_MENU_ITEMS_BEFORE_ORDER_PLACEMENT")
                                                        .interactionType("API_CALL")
                                                        .status("BUSINESS_FAILURE")
                                                        .errorMessage("Invalid menu item IDs provided")
                                                        .timestamp(timestamp)
                                                        .build();
                                        businessEventLoggingService.saveBusinessEvent(eventLog);
                                        throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                                                        "Invalid menu item IDs provided",
                                                        "menuItemId");
                                }
                        }

                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("VALIDATE_CART_AND_MENU_ITEMS_BEFORE_ORDER_PLACEMENT")
                                        .interactionType("API_CALL")
                                        .status("BUSINESS_SUCCESS")
                                        .timestamp(timestamp)
                                        .build();
                        businessEventLoggingService.saveBusinessEvent(eventLog);

                        return APIResponse.success(true, "Validated cart and menu items successfully", requestId,
                                        timestamp,
                                        HttpStatus.OK);

                } catch (ApplicationException e) {

                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("VALIDATE_CART_AND_MENU_ITEMS_BEFORE_ORDER_PLACEMENT")
                                        .interactionType("API_CALL")
                                        .status("BUSINESS_FAILURE")
                                        .errorMessage(e.getMessage())
                                        .timestamp(timestamp)
                                        .build();
                        businessEventLoggingService.saveBusinessEvent(eventLog);
                        return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp,
                                        HttpStatus.NOT_FOUND);
                } catch (Exception e) {
                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("VALIDATE_CART_AND_MENU_ITEMS_BEFORE_ORDER_PLACEMENT")
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

        public ResponseEntity<APIResponse<Map<String, CartStatus>>> updateCartStatus(Long cartId, CartStatus cartStatus,
                        String requestId) {

                String timestamp = dateTimeUtil.getDateTime();
                String spanId = UUID.randomUUID().toString();

                try {

                        Cart cart = cartRepository.findById(cartId)
                                        .orElseThrow(new Supplier<ApplicationException>() {
                                                @Override
                                                public ApplicationException get() {

                                                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                                                        .requestId(requestId)
                                                                        .spanId(spanId)
                                                                        .eventName("UPDATE_CART_STATUS")
                                                                        .interactionType("API_CALL")
                                                                        .status("BUSINESS_FAILURE")
                                                                        .errorMessage("Invalid cart ID provided")
                                                                        .timestamp(timestamp)
                                                                        .build();
                                                        businessEventLoggingService.saveBusinessEvent(eventLog);

                                                        return new ApplicationException(
                                                                        ErrorCode.RESOURCE_NOT_FOUND,
                                                                        "Invalid cart ID provided",
                                                                        "cartId");
                                                }
                                        });

                        cart.setUpdatedAt(timestamp);
                        cart.setCartStatus(cartStatus);
                        cart = cartRepository.save(cart);
                        Map<String, CartStatus> response = new HashMap<>();
                        response.put("updatedStatus", cartStatus);

                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("UPDATE_CART_STATUS")
                                        .interactionType("API_CALL")
                                        .status("BUSINESS_SUCCESS")
                                        .timestamp(timestamp)
                                        .build();
                        businessEventLoggingService.saveBusinessEvent(eventLog);

                        return APIResponse.success(response, "Cart status updated successfully", requestId, timestamp,
                                        HttpStatus.OK);

                } catch (ApplicationException e) {

                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("UPDATE_CART_STATUS")
                                        .interactionType("API_CALL")
                                        .status("BUSINESS_FAILURE")
                                        .errorMessage(e.getMessage())
                                        .timestamp(timestamp)
                                        .build();
                        businessEventLoggingService.saveBusinessEvent(eventLog);

                        return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp,
                                        HttpStatus.NOT_FOUND);
                } catch (Exception e) {

                        BusinessEventLog eventLog = BusinessEventLog.builder()
                                        .requestId(requestId)
                                        .spanId(spanId)
                                        .eventName("UPDATE_CART_STATUS")
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
}
