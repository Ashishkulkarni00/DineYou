package menucard_service.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import menucard_service.dto.APIResponse;
import menucard_service.dto.MenuCardReqDto;
import menucard_service.dto.SearchMenuItemResultsResDto;
import menucard_service.model.MenuItem;
import menucard_service.model.Menucard;
import menucard_service.service.SearchMenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
public class SearchMenuItemController {

    @Autowired
    private SearchMenuItemService searchMenuItemService;

    @GetMapping("/item")
    public ResponseEntity<APIResponse<List<SearchMenuItemResultsResDto>>> searchMenuItems(
            @RequestParam(name = "itemName") String query,
            HttpServletRequest request) {
        String requestId = (String) request.getAttribute("requestId");
        return searchMenuItemService.getSearchedMenuItems(query, requestId);
    }

}
