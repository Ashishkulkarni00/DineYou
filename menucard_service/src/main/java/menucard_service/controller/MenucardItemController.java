package menucard_service.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import menucard_service.dto.APIResponse;
import menucard_service.model.MenuItem;
import menucard_service.service.MenucardItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/menucardItem")
@CrossOrigin(origins = "*")
public class MenucardItemController {

    @Autowired
    private MenucardItemService menucardItemService;


    @PostMapping("/add/{menucardId}/{itemCategoryId}")
    public ResponseEntity<APIResponse<MenuItem>> addMenuItemWithCategory(
            @PathVariable("menucardId") Long menucardId,
            @PathVariable("itemCategoryId") Long itemCategoryId,
            @RequestBody @Valid MenuItem menuItem,
            HttpServletRequest request) {
        String requestId = (String) request.getAttribute("requestId");
        return menucardItemService.addMenucardItem(menucardId,itemCategoryId,menuItem,requestId);
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse<MenuItem>> updateMenuItem(
            @RequestBody @Valid MenuItem menuItem,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return menucardItemService.updateMenuItem(menuItem, requestId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<MenuItem>> getMenuItem(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return menucardItemService.getMenuItem(id, requestId);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteMenuItem(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return menucardItemService.deleteMenuItem(id, requestId);
    }

    @GetMapping("/getPopularItems/{restaurantId}")
    public ResponseEntity<APIResponse<List<MenuItem>>> getPopularItemsForLandingPage(
            @PathVariable("restaurantId") Long restaurantId,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return menucardItemService.getPopularItemsForRestaurant(restaurantId, requestId);
    }

    //Helper API
    @GetMapping("/getMenuItemsByIds")
    public ResponseEntity<APIResponse<List<MenuItem>>> getAllMenuItemsByIds(
            @RequestParam("menuItemIds") List<Long> menuItemIds,
            HttpServletRequest request
            ){
        String requestId = (String) request.getAttribute("requestId");
        return menucardItemService.getAllMenuItemsByIds(menuItemIds, requestId);
    }


}
