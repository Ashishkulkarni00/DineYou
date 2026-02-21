package menucard_service.service;

import menucard_service.dto.APIResponse;
import menucard_service.dto.MenuCardReqDto;
import menucard_service.enums.ErrorCode;
import menucard_service.exception.ApplicationException;
import menucard_service.model.BusinessEventLog;
import menucard_service.model.ItemCategory;
import menucard_service.model.MenuItem;
import menucard_service.model.Menucard;
import menucard_service.repository.MenucardRepository;
import menucard_service.util.DateTimeUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MenucardService {

    /* Notes
     1. THERE IS NOTHING AS SUCH NEED TO UPDATE MENU-CARD

     */

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MenucardRepository menucardRepository;

    @Autowired
    private RestaurantServiceClient restaurantServiceClient;

    @Autowired
    private BusinessEventLoggingService businessEventLoggingService;

    public ResponseEntity<APIResponse<Menucard>> createMenucardWithCategoryAndMenuItems(MenuCardReqDto menuCardReqDto, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();

        try {

            //This checks whether menu card is already present for given restaurant or not
            if (menucardRepository.findByRestaurantId(menuCardReqDto.getRestaurantId()).isPresent()) {
                throw new ApplicationException(
                        ErrorCode.DUPLICATE_RESOURCE,
                        "Menucard already exists for this restaurant",
                        "restaurantId"
                );
            }
            //This checks whether restaurant itself present or not
            if(!restaurantServiceClient.doesRestaurantExist(menuCardReqDto.getRestaurantId(),requestId)){
                throw new ApplicationException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Invalid restaurant Id provided",
                        "restaurantId"
                );
            }

            Menucard menuCard = new Menucard();
            menuCard.setRestaurantId(menuCardReqDto.getRestaurantId());
            menuCard.setCreatedAt(timestamp);
            menuCard.setUpdatedAt(timestamp);

            List<ItemCategory> categoryList = menuCardReqDto.getCategories().stream().map(categoryDTO -> {
                ItemCategory category = new ItemCategory();
                category.setCategoryName(categoryDTO.getCategoryName());
                category.setDescription(categoryDTO.getDescription());
                category.setCreatedAt(timestamp);
                category.setUpdatedAt(timestamp);
                category.setMenucard(menuCard);

                List<MenuItem> items = categoryDTO.getItems().stream().map(itemDTO -> {
                    MenuItem item = modelMapper.map(itemDTO, MenuItem.class);
                    item.setCreatedAt(timestamp);
                    item.setUpdatedAt(timestamp);
                    item.setCategory(category);
                    return item;
                }).collect(Collectors.toList());

                category.setMenuItemList(items);
                return category;
            }).collect(Collectors.toList());

            menuCard.setCategories(categoryList);
            Menucard menucard = menucardRepository.save(menuCard);

            return APIResponse.success(menucard, "Menucard created successfully", requestId, timestamp, HttpStatus.CREATED);

        } catch (ApplicationException e) {
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return APIResponse.error(ErrorCode.DATA_INTEGRITY_VIOLATION, e.getMessage(), requestId, timestamp, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<APIResponse<Menucard>> getMenucard(Long id, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();

        try{
            Optional<Menucard> existingMenucard = menucardRepository.findById(id);
            if(existingMenucard.isPresent()){
                return APIResponse.success(existingMenucard.get(),"Menucard fetched successfully",requestId,timestamp,HttpStatus.OK);
            }else {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Invalid menucard id","menucardId");
            }
        }
        catch (ApplicationException e){
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Creating just menu card
    public ResponseEntity<APIResponse<Menucard>> createMenuCard(Menucard menucard, String requestId) {
        String timestamp = dateTimeUtil.getDateTime();

        try{

            //This checks whether menu card is already present for given restaurant or not
            if (menucardRepository.findByRestaurantId(menucard.getRestaurantId()).isPresent()) {
                throw new ApplicationException(
                        ErrorCode.DUPLICATE_RESOURCE,
                        "Menucard already exists for this restaurant",
                        "restaurantId"
                );
            }

            //This checks whether restaurant itself present or not
            if(!restaurantServiceClient.doesRestaurantExist(menucard.getRestaurantId(), requestId)){
                throw new ApplicationException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Invalid restaurant Id provided",
                        "restaurantId"
                );
            }

            menucard.setCreatedAt(timestamp);
            menucard.setUpdatedAt(timestamp);
            menucard = menucardRepository.save(menucard);
            return APIResponse.success(menucard,"Menucard created successfully",requestId,timestamp,HttpStatus.CREATED);

        }catch (ApplicationException e){
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<APIResponse<Void>> deleteMenucard(Long menucardId, String requestId){
        String timestamp = dateTimeUtil.getDateTime();
        try{
            if(menucardRepository.existsById(menucardId)){
                menucardRepository.deleteById(menucardId);
                return APIResponse.success(null,"Menucard deleted successfully",requestId,timestamp,HttpStatus.OK);
            }else {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Invalid menucard id","menucardId");
            }
        }
        catch (ApplicationException e){
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<APIResponse<Menucard>> getMenucardByRestaurantId(Long restaurantId, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();

        try{

           Optional<Menucard> menucard = menucardRepository.findByRestaurantId(restaurantId);

           if(menucard.isPresent()){

               BusinessEventLog eventLog = BusinessEventLog.builder()
                       .requestId(requestId)
                       .spanId(spanId)
                       .eventName("FETCH_CATEGORY_WISE_MENUCARD")
                       .interactionType("API_CALL")
                       .status("BUSINESS_SUCCESS")
                       .timestamp(timestamp)
                       .build();
               businessEventLoggingService.saveBusinessEvent(eventLog);

               return APIResponse.success(menucard.get(),"Menucard fetched successfully",requestId,timestamp,HttpStatus.OK);
           }else {

               BusinessEventLog eventLog = BusinessEventLog.builder()
                       .requestId(requestId)
                       .spanId(spanId)
                       .eventName("FETCH_CATEGORY_WISE_MENUCARD")
                       .interactionType("API_CALL")
                       .status("BUSINESS_FAILURE")
                       .errorMessage("Menu card not found for given restaurant id")
                       .timestamp(timestamp)
                       .build();

               businessEventLoggingService.saveBusinessEvent(eventLog);

               throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Menu card not found for given restaurant id","restaurantId");
           }


        } catch (ApplicationException e){

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_CATEGORY_WISE_MENUCARD")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);

            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        }
        catch (Exception e){

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_CATEGORY_WISE_MENUCARD")
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
