package menucard_service.service;

import menucard_service.dto.APIResponse;
import menucard_service.enums.ErrorCode;
import menucard_service.exception.ApplicationException;
import menucard_service.model.BusinessEventLog;
import menucard_service.model.ItemCategory;
import menucard_service.model.MenuItem;
import menucard_service.repository.ItemCategoryRepository;
import menucard_service.repository.MenuItemRepository;
import menucard_service.repository.MenucardRepository;
import menucard_service.util.DateTimeUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MenucardItemService {

    @Autowired
    private MenuItemRepository itemRepository;

    @Autowired
    private MenucardRepository menucardRepository;

    @Autowired
    private ItemCategoryRepository itemCategoryRepository;

    @Autowired
    private BusinessEventLoggingService businessEventLoggingService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private DateTimeUtil dateTimeUtil;


    public ResponseEntity<APIResponse<MenuItem>> addMenucardItem(Long menucardId, Long itemCategoryId, MenuItem menuItem, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();

        try {
            if (menucardId == null) {
                throw new ApplicationException(ErrorCode.MISSING_REQUIRED_FIELD, "Please provide menucard id", "menucardId");
            }

            if (!menucardRepository.existsById(menucardId)) {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid menucard ID", "menucardId");
            }

            if (itemCategoryId == null) {
                throw new ApplicationException(ErrorCode.MISSING_REQUIRED_FIELD, "Please provide item category id", "itemCategoryId");
            }

            Optional<ItemCategory> existingCategory = itemCategoryRepository.findById(itemCategoryId);
            if (existingCategory.isEmpty()) {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid item category ID", "itemCategoryId");
            }

            if (menuItem.getItemName() == null || menuItem.getItemName().isBlank()) {
                throw new ApplicationException(ErrorCode.MISSING_REQUIRED_FIELD, "Item name is required", "itemName");
            }

            MenuItem newMenuItem = modelMapper.map(menuItem, MenuItem.class);
            newMenuItem.setCreatedAt(timestamp);
            newMenuItem.setUpdatedAt(timestamp);
            newMenuItem.setCategory(existingCategory.get());

            newMenuItem = itemRepository.save(newMenuItem);
            existingCategory.get().getMenuItemList().add(newMenuItem);
            itemCategoryRepository.save(existingCategory.get());

            return APIResponse.success(newMenuItem, "Menu item created successfully", requestId, timestamp, HttpStatus.CREATED);

        } catch (ApplicationException e) {
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<APIResponse<MenuItem>> getMenuItem(Long menuItemId, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();
        try {
            Optional<MenuItem> menuItem = itemRepository.findById(menuItemId);
            if (menuItem.isPresent()) {
                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("FETCH_MENU_ITEM")
                        .interactionType("API_CALL")
                        .status("BUSINESS_SUCCESS")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);
                return APIResponse.success(menuItem.get(), "Menu item fetched successfully", requestId, timestamp, HttpStatus.OK);
            } else {
                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("FETCH_MENU_ITEM")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Invalid menu item ID provided")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid menu item ID provided", "menuItemId");
            }
        } catch (ApplicationException e) {
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_MENU_ITEM")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_MENU_ITEM")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<APIResponse<MenuItem>> updateMenuItem(MenuItem menuItem, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();

        try {

            if (menuItem.getItemId() == null) {
                throw new ApplicationException(ErrorCode.MISSING_REQUIRED_FIELD, "Please provide menu item id", "menuItemId");
            }

            Optional<MenuItem> existingItem = itemRepository.findById(menuItem.getItemId());

            if (!existingItem.isPresent()) {

                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Please provide valid menu item id", "menuItemId");

            } else {

                modelMapper.map(menuItem, existingItem.get());
                menuItem = itemRepository.save(existingItem.get());

                return APIResponse.success(menuItem, "Menu item updated successfully", requestId, timestamp, HttpStatus.CREATED);

            }

        } catch (ApplicationException e) {
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


    public ResponseEntity<APIResponse<Void>> deleteMenuItem(Long id, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();

        try {

            if (id == null) {

                throw new ApplicationException(
                        ErrorCode.MISSING_REQUIRED_FIELD,
                        "Please provide menu item id",
                        "menuItemId"
                );

            }

            if (!itemRepository.existsById(id)) {

                throw new ApplicationException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Please provide valid menu item id",
                        "menuItemId"
                );


            } else {

                itemRepository.deleteById(id);
                return APIResponse.success(null, "Menu item deleted successfully", requestId, timestamp, HttpStatus.OK);

            }

        } catch (ApplicationException e) {
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<APIResponse<List<MenuItem>>> getAllMenuItemsByIds(List<Long> menuItemIds, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();

        try {
            List<MenuItem> menuItems = itemRepository.findAllById(menuItemIds);
            if (menuItems.isEmpty()) {
                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("FETCH_MENU_ITEMS_BY_IDs")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Menu items not found")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Menu items not found", "menuItemIds");
            }

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_MENU_ITEMS_BY_IDs")
                    .interactionType("API_CALL")
                    .status("BUSINESS_SUCCESS")
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.success(menuItems, "Menu items fetched successfully", requestId, timestamp, HttpStatus.OK);

        } catch (ApplicationException e) {
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_MENU_ITEMS_BY_IDs")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_MENU_ITEMS_BY_IDs")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<APIResponse<List<MenuItem>>> getPopularItemsForRestaurant(Long restaurantId, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();

        try {
            List<MenuItem> menuItems = itemRepository.findPopularItemsForRestaurant(restaurantId);
            if (menuItems.isEmpty()) {
                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("FETCH_POPULAR_MENU_ITEMS")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Popular items not found")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Popular items not found", "popularItems");
            }

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_POPULAR_MENU_ITEMS")
                    .interactionType("API_CALL")
                    .status("BUSINESS_SUCCESS")
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);

            return APIResponse.success(menuItems, "popular items fetched successfully", requestId, timestamp, HttpStatus.OK);

        } catch (ApplicationException e) {

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_POPULAR_MENU_ITEMS")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);

            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_POPULAR_MENU_ITEMS")
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
