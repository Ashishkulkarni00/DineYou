package restaurant_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant_service.dto.APIResponse;
import restaurant_service.model.Restaurant;
import restaurant_service.service.RestaurantService;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/v1/restaurant")
@CrossOrigin("*")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse<Restaurant>> createRestaurant(
            @RequestBody @Valid Restaurant restaurant,
            HttpServletRequest request) {
        String requestId = (String) request.getAttribute("requestId");
        return restaurantService.createRestaurant(restaurant, requestId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Restaurant>> getRestaurant(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return restaurantService.getRestaurant(id, requestId);
    }


    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<Restaurant>> updateRestaurant(
            @PathVariable("id") Long id,
            @RequestBody @Valid Restaurant restaurant,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return restaurantService.updateRestaurant(id, restaurant, requestId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteRestaurant(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return restaurantService.deleteRestaurant(id,requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<APIResponse<List<Restaurant>>> getAllRestaurants(
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return restaurantService.getAllRestaurant(requestId);
    }


    //Helper methods
    @GetMapping("/{restaurantId}/exists")
    public ResponseEntity<APIResponse<Map<String, Boolean>>> checkRestaurantExistence
    (@PathVariable("restaurantId") Long restaurantId, HttpServletRequest request){
        String requestId = (String) request.getAttribute("requestId");
        return restaurantService.checkExistence(requestId, restaurantId);
    }

}
