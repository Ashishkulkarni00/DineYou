package menucard_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import menucard_service.dto.APIResponse;
import menucard_service.model.ItemCategory;
import menucard_service.service.MenucardItemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/menucardCategory")
@CrossOrigin(origins = "*")
public class MenucardCategoryController {

    @Autowired
    private MenucardItemCategoryService menucardItemCategoryService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse<ItemCategory>> createNewMenucardItemCategory(
            @RequestBody @Valid ItemCategory itemCategory,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return menucardItemCategoryService.createNewMenucardCategory(itemCategory, requestId);
    }


    @PutMapping("/update")
    public ResponseEntity<APIResponse<ItemCategory>> updateCategory(
            @RequestBody @Valid ItemCategory itemCategory,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return menucardItemCategoryService.updateItemCategory(itemCategory, requestId);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteItemCategory(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return menucardItemCategoryService.deleteItemCategory(id, requestId);
    }

}
