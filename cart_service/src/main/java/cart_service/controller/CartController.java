package cart_service.controller;

import cart_service.dto.APIResponse;
import cart_service.dto.CreateCartReqDto;
import cart_service.dto.OrderPlacementReqDto;
import cart_service.dto.UpdateCartReqDto;
import cart_service.enums.CartStatus;
import cart_service.model.Cart;
import cart_service.model.CartItem;
import cart_service.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@CrossOrigin("*")
public class CartController {


    @Autowired
    private CartService cartService;


    @PostMapping("/create")
    public ResponseEntity<APIResponse<Cart>> createCart(
            @RequestBody @Valid CreateCartReqDto createCartReqDto,
            HttpServletRequest request) {
        String requestId = (String) request.getAttribute("requestId");
        return cartService.createCart(createCartReqDto, requestId);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<APIResponse<Cart>> getCartByUserId(
            @PathVariable("userId") String userId,
            HttpServletRequest request
    ) {
        String requestId = (String) request.getAttribute("requestId");
        return cartService.getCartWithCartItems(userId, requestId);
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteCart(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ) {
        String requestId = (String) request.getAttribute("requestId");
        return cartService.deleteCart(id, requestId);
    }


    @PutMapping("/update")
    public ResponseEntity<APIResponse<CartItem>> updateCart(
            @RequestBody @Valid UpdateCartReqDto updateCartReqDto,
            HttpServletRequest request
    ) {
        String requestId = (String) request.getAttribute("requestId");
        return cartService.updateCart(updateCartReqDto, requestId);
    }


    @GetMapping("/calculateTotal/{id}")
    public ResponseEntity<APIResponse<Map<String, Double>>> getCartTotal(
            @PathVariable("id") Long cartId,
            HttpServletRequest request
    ) {
        String requestId = (String) request.getAttribute("requestId");
        return cartService.calculateCartTotal(cartId, requestId);
    }


    //helper method
    @PostMapping("/validate-cart-and-menu-items")
    public ResponseEntity<APIResponse<Boolean>> validateCartAndMenuItems(
            @RequestBody OrderPlacementReqDto orderPlacementReqDto,
            HttpServletRequest request
    ) {
        String requestId = (String) request.getAttribute("requestId");
        return cartService.validateCartIdAndMenuItemsForPlacingOrder(orderPlacementReqDto, requestId);
    }

    //helper method
    @PutMapping("/updateCartStatus")
    public ResponseEntity<APIResponse<Map<String, CartStatus>>> updateCartStatus(
            @RequestParam("cartId") Long cartId,
            @RequestParam("cartStatus") CartStatus cartStatus,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return cartService.updateCartStatus(cartId, cartStatus, requestId);
    }

}
