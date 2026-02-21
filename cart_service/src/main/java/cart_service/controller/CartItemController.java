package cart_service.controller;

import cart_service.dto.APIResponse;
import cart_service.dto.AddToCartReqDto;
import cart_service.model.CartItem;
import cart_service.service.CartItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cartItem")
@CrossOrigin("*")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;


    @PostMapping("/addCartItem")
    public ResponseEntity<APIResponse<CartItem>> addToCart(
            @RequestBody @Valid AddToCartReqDto addToCartReqDto,
            HttpServletRequest request) {
        String requestId = (String) request.getAttribute("requestId");
        return cartItemService.addToCart(addToCartReqDto, requestId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> removeCartItem(
            @PathVariable("id") Long cartItemId,
            HttpServletRequest request) {
        String requestId = (String) request.getAttribute("requestId");
        return cartItemService.removeCartItem(cartItemId, requestId);
    }

}
