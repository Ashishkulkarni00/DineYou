package restaurant_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant_service.dto.APIResponse;
import restaurant_service.dto.RestaurantDiscountReqDto;
import restaurant_service.model.RestaurantDiscount;
import restaurant_service.service.RestaurantDiscountService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurant-discount")
public class DiscountController {

    @Autowired
    private RestaurantDiscountService discountService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse<RestaurantDiscount>> createRestaurantDiscount(
            @RequestBody @Valid RestaurantDiscountReqDto restaurantDiscountReqDto,
            HttpServletRequest request
            ){
        String requestId = (String) request.getAttribute("requestId");
        return discountService.createDiscount(restaurantDiscountReqDto, requestId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<RestaurantDiscount>> getDiscount(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return discountService.getDiscount(id,requestId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<RestaurantDiscount>> updateRestaurantDiscount(
            @PathVariable("id") Long id,
            @RequestBody @Valid RestaurantDiscountReqDto restaurantDiscountReqDto,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return discountService.updateDiscount(id, restaurantDiscountReqDto, requestId);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteDiscount(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return discountService.deleteDiscount(id, requestId);
    }

    @GetMapping("/by-restaurant/{restaurantId}")
    public ResponseEntity<APIResponse<List<RestaurantDiscount>>> getDiscountsByRestaurant(
            @PathVariable("restaurantId") Long restaurantId,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return discountService.getDiscountsByRestaurantId(restaurantId, requestId);
    }

    @GetMapping("/active/by-restaurant/{restaurantId}")
    public ResponseEntity<APIResponse<List<RestaurantDiscount>>> getActiveDiscountsByRestaurant(
            @PathVariable("restaurantId") Long restaurantId,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return discountService.getActiveDiscountsByRestaurantId(restaurantId, requestId);
    }

}
