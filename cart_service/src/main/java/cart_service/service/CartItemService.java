package cart_service.service;

import cart_service.dto.APIResponse;
import cart_service.dto.AddToCartReqDto;
import cart_service.enums.CartItemStatus;
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
import java.util.Optional;
import java.util.UUID;

@Service
public class CartItemService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private MenucardServiceClient menucardServiceClient;

    @Autowired
    private RequestInfoProvider requestInfoProvider;

    @Autowired
    private BusinessEventLoggingService businessEventLoggingService;


    public ResponseEntity<APIResponse<CartItem>> addToCart(AddToCartReqDto addToCartReqDto, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();

        try {
            //TODO Validate userId - when user management integrated
            //TODO validate tableId - when table management integrated

            Cart cart = null;
            Optional<Cart> optionalCart = cartRepository.findById(addToCartReqDto.getCartId());

            if (optionalCart.isPresent()) {
                cart = optionalCart.get();
            } else {

                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("ADD_ITEM_TO_CART")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Invalid cart ID provided")
                        .timestamp(timestamp)
                        .build();

                businessEventLoggingService.saveBusinessEvent(eventLog);

                throw new ApplicationException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Invalid cart ID provided",
                        "cartId"
                );
            }

            if(!menucardServiceClient.doesMenuItemExist(addToCartReqDto.getMenuItemId(), requestId)){
                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("ADD_ITEM_TO_CART")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Invalid menu item ID provided")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);

                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Invalid menu item ID provided", "menuItemId");
            }

            CartItem cartItem = null;
            boolean isQuantityUpdate = false;
            if(addToCartReqDto.getCartItemId() == null) {
                cartItem = new CartItem();
            }else{
                isQuantityUpdate = true;
                cartItem = cartItemRepository.findById(addToCartReqDto.getCartItemId()).get();
            }
            modelMapper.map(addToCartReqDto, cartItem);
            cartItem.setItemStatus(CartItemStatus.ADDED);
            cartItem.setAnonymousSessionId(requestInfoProvider.getAnonymousSessionId());
            cartItem.setRequestId(requestId);
            cartItem.setKeycloakSessionId(requestInfoProvider.getKeycloakSessionId());
            cartItem.setCart(cart);
            cartItem.setAddedAt(timestamp);
            cartItem.setUpdatedAt(timestamp);
            cartItem = cartItemRepository.save(cartItem);

            if(isQuantityUpdate) {
                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("UPDATE_ITEM_QUANTITY_OF_CART")
                        .interactionType("API_CALL")
                        .status("BUSINESS_SUCCESS")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);
            }else{
                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("ADD_ITEM_TO_CART")
                        .interactionType("API_CALL")
                        .status("BUSINESS_SUCCESS")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);
            }

            return APIResponse.success(cartItem, "Cart item added successfully", requestId, timestamp, HttpStatus.OK);

        }catch (ApplicationException e) {
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("ADD_ITEM_TO_CART")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("ADD_ITEM_TO_CART")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    public ResponseEntity<APIResponse<Void>> removeCartItem(Long cartItemId, String requestId){

        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();

        try{
            Optional<CartItem> existingItem = cartItemRepository.findById(cartItemId);
            if(existingItem.isPresent()){
                existingItem.get().setItemStatus(CartItemStatus.REMOVED);
                cartItemRepository.save(existingItem.get());

                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("REMOVE_ITEM_FROM_CART")
                        .interactionType("API_CALL")
                        .status("BUSINESS_SUCCESS")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);

                return APIResponse.success(null, "Cart item removed successfully", requestId, timestamp, HttpStatus.OK);
            }else{

                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("REMOVE_ITEM_FROM_CART")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Invalid cart item ID provided")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid cart item ID provided", "cartItemId");
            }

        }catch (ApplicationException e) {
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("REMOVE_ITEM_FROM_CART")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        
        } catch (Exception e) {
            e.printStackTrace();
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("REMOVE_ITEM_FROM_CART")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
