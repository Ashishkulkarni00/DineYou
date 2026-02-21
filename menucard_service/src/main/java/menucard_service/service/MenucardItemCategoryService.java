package menucard_service.service;

import menucard_service.dto.APIResponse;
import menucard_service.enums.ErrorCode;
import menucard_service.exception.ApplicationException;
import menucard_service.model.ItemCategory;
import menucard_service.model.Menucard;
import menucard_service.repository.ItemCategoryRepository;
import menucard_service.repository.MenucardRepository;
import menucard_service.util.DateTimeUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MenucardItemCategoryService {

     /* Notes
     1. THERE IS NOTHING AS SUCH NEED TO GET MENU-CARD-CATEGORY INDIVIDUALLY

     */

    @Autowired
    private MenucardRepository menucardRepository;

    @Autowired
    private ItemCategoryRepository itemCategoryRepository;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private ModelMapper modelMapper;

    //create new category for existing menucard
    public ResponseEntity<APIResponse<ItemCategory>> createNewMenucardCategory(ItemCategory itemCategory, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();

        try {

            // validating request body, whether menucard details are provided or not
            if (itemCategory.getMenucard() == null) {

                throw new ApplicationException(
                        ErrorCode.MISSING_REQUIRED_FIELD,
                        "Please provide menucard details",
                        "menucard"
                );

            }

            // validating whether menu card id is provided or not
            if (itemCategory.getMenucard().getMenucardId() == null) {

                throw new ApplicationException(
                        ErrorCode.MISSING_REQUIRED_FIELD,
                        "Please provide menucard id",
                        "menucardId"
                );

            }

            // validating menu card id
            if (!menucardRepository.existsById(itemCategory.getMenucard().getMenucardId())) {

                throw new ApplicationException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Please provide valid menucard id",
                        "menucardId"
                );

            }

            Optional<Menucard> existingMenucard = menucardRepository.findById(itemCategory.getMenucard().getMenucardId());
            ItemCategory category = new ItemCategory();
            modelMapper.map(itemCategory, category);
            category.setMenucard(existingMenucard.get());
            category = itemCategoryRepository.save(category);
            return APIResponse.success(category, "Menucard category created successfully", requestId, timestamp, HttpStatus.CREATED);

        } catch (ApplicationException e) {
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<APIResponse<ItemCategory>> updateItemCategory(ItemCategory itemCategory, String requestId) {
        // ONLY ALLOWED TO UPDATE CATEGORY NAME AND ITS DESCRIPTION

        String timestamp = dateTimeUtil.getDateTime();


        try {

            if (itemCategory.getCategoryId() == null) {

                throw new ApplicationException(
                        ErrorCode.MISSING_REQUIRED_FIELD,
                        "Please provide item category id",
                        "itemCategoryId"
                );

            }

            Optional<ItemCategory> existingCategory = itemCategoryRepository.findById(itemCategory.getCategoryId());

            if(!existingCategory.isPresent()){

                throw new ApplicationException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Please provide valid item category id",
                        "itemCategoryId"
                );

            }else{

                existingCategory.get().setCategoryName(itemCategory.getCategoryName());
                existingCategory.get().setDescription(itemCategory.getDescription());
                existingCategory.get().setUpdatedAt(timestamp);
                itemCategory = itemCategoryRepository.save(existingCategory.get());

                return APIResponse.success(itemCategory,"Item category updated successfully",requestId,timestamp,HttpStatus.OK);

            }

        } catch (ApplicationException e) {
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    public ResponseEntity<APIResponse<Void>> deleteItemCategory(Long id, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();

        try {

            if (id == null) {

                throw new ApplicationException(
                        ErrorCode.MISSING_REQUIRED_FIELD,
                        "Please provide item category id",
                        "itemCategoryId"
                );

            }

            if(!itemCategoryRepository.existsById(id)){

                throw new ApplicationException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Please provide valid item category id",
                        "itemCategoryId"
                );


            }else{

                itemCategoryRepository.deleteById(id);
                return APIResponse.success(null,"Item category deleted successfully",requestId,timestamp,HttpStatus.OK);

            }

        } catch (ApplicationException e) {
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }





}
