package menucard_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import menucard_service.dto.APIResponse;
import menucard_service.dto.MenuCardReqDto;
import menucard_service.model.Menucard;
import menucard_service.service.MenucardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/menucard")
public class MenuCardController {

    @Autowired
    private MenucardService menucardService;

    @PostMapping("/createMenucardWithCategoryAndMenuItems")
    public ResponseEntity<APIResponse<Menucard>> createRestaurantWithCategoryAndMenuItems(
            @RequestBody @Valid MenuCardReqDto menuCardReqDto,
            HttpServletRequest request) {
        String requestId = (String) request.getAttribute("requestId");
        return menucardService.createMenucardWithCategoryAndMenuItems(menuCardReqDto, requestId);
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse<Menucard>> createMenucard(
            @RequestBody @Valid Menucard menucard,
            HttpServletRequest request) {
        String requestId = (String) request.getAttribute("requestId");
        return menucardService.createMenuCard(menucard, requestId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Menucard>> getMenucard(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return menucardService.getMenucard(id, requestId);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<APIResponse<Menucard>> getMenucardByRestaurantId(
            @PathVariable("restaurantId") Long restaurantId,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return menucardService.getMenucardByRestaurantId(restaurantId, requestId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteMenucard(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return menucardService.deleteMenucard(id, requestId);
    }

}
