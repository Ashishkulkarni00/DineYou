package restaurant_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant_service.dto.APIResponse;
import restaurant_service.dto.LandingPageResDto;
import restaurant_service.service.LandingPageService;


@RestController
@RequestMapping("/api/v1/restaurantLandingPage")
@CrossOrigin("*")
public class LandingPageController {


    @Autowired
    private LandingPageService landingPageService;

    @GetMapping("/getDetails/{restaurantId}")
    public ResponseEntity<APIResponse<LandingPageResDto>> getPopularItems(
            @PathVariable("restaurantId") Long restaurantId,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return landingPageService.getLandingPageDetails(requestId, restaurantId);
    }


}
