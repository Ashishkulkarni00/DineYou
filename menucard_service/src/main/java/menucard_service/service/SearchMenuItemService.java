package menucard_service.service;

import menucard_service.dto.APIResponse;
import menucard_service.dto.SearchMenuItemResultsResDto;
import menucard_service.enums.ErrorCode;
import menucard_service.exception.ApplicationException;
import menucard_service.model.MenuItem;
import menucard_service.repository.MenuItemRepository;
import menucard_service.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchMenuItemService {

    @Autowired
    DateTimeUtil dateTimeUtil;

    @Autowired
    MenuItemRepository menuItemRepository;


    public ResponseEntity<APIResponse<List<SearchMenuItemResultsResDto>>> getSearchedMenuItems(String query, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();
        try {
            List<MenuItem> menuItems = menuItemRepository.findBySearchedQuery(query);

            if (menuItems.isEmpty()) {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Searched items not found", "searchedItems");
            }

            List<SearchMenuItemResultsResDto> resultsResDtoList = new ArrayList<>();
            for(MenuItem menuItem : menuItems){
                SearchMenuItemResultsResDto resDto = new SearchMenuItemResultsResDto();
                resDto.setItemId(menuItem.getItemId());
                resDto.setItemName(menuItem.getItemName());
                resDto.setItemPrice(menuItem.getItemPrice());
                resDto.setDescription(menuItem.getItemDescription());
                resDto.setImageUrl(menuItem.getImagePath());
                resultsResDtoList.add(resDto);
            }

            return APIResponse.success(resultsResDtoList, "Searched menu items fetched successfully", requestId, timestamp, HttpStatus.OK);

        } catch (ApplicationException e) {
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
