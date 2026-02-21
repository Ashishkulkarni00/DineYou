package restaurant_service.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import restaurant_service.dto.APIResponse;
import restaurant_service.enums.ErrorCode;
import restaurant_service.exception.ApplicationException;
import restaurant_service.model.BusinessEventLog;
import restaurant_service.model.Restaurant;
import restaurant_service.repository.RestaurantRepository;
import restaurant_service.util.DateTimeUtil;

import java.util.*;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    BusinessEventLoggingService businessEventLoggingService;


    public ResponseEntity<APIResponse<Restaurant>> createRestaurant(Restaurant restaurant, String requestId) {
        String timestamp = dateTimeUtil.getDateTime();
        try {

            if (restaurantRepository.existsByName(restaurant.getName())) {
                throw new ApplicationException(
                        ErrorCode.DUPLICATE_RESOURCE,
                        "A restaurant with the same name already exists",
                        "name"
                );
            }
            restaurant.setCreatedAt(timestamp);
            Restaurant saved = restaurantRepository.save(restaurant);
            return APIResponse.success(saved, "Restaurant created successfully", requestId, timestamp, HttpStatus.CREATED);

        } catch (ApplicationException e) {
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return APIResponse.error(ErrorCode.DATA_INTEGRITY_VIOLATION, e.getMessage(), requestId, timestamp, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<APIResponse<Restaurant>> getRestaurant(Long id, String requestId) {
        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();

        try{
            Optional<Restaurant> existingRestaurant = restaurantRepository.findById(id);
            if(existingRestaurant.isPresent()){

                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("FETCH_RESTAURANT")
                        .interactionType("API_CALL")
                        .status("BUSINESS_SUCCESS")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);

                return APIResponse.success(existingRestaurant.get(),"Restaurant fetched successfully",requestId,timestamp,HttpStatus.OK);
            }else {
                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("FETCH_LANDING_PAGE")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Invalid restaurant id")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);

                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Invalid restaurant id","restaurantId");
            }
        }
        catch (ApplicationException e){
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_LANDING_PAGE")
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
                    .eventName("FETCH_LANDING_PAGE")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);

            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<APIResponse<Restaurant>> updateRestaurant(Long id, Restaurant restaurant, String requestId) {
        String timestamp = dateTimeUtil.getDateTime();
        try{
            Optional<Restaurant> existingRestaurant = restaurantRepository.findById(id);
            if(existingRestaurant.isPresent()){
                modelMapper.map(restaurant, existingRestaurant.get());
                existingRestaurant.get().setUpdatedAt(timestamp);
                restaurantRepository.save(existingRestaurant.get());
                return APIResponse.success(existingRestaurant.get(),"Restaurant updated successfully",requestId,timestamp,HttpStatus.OK);
            }else {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Invalid restaurant id","restaurantId");
            }
        }catch (ApplicationException e){
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<APIResponse<Void>> deleteRestaurant(Long id, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();

        try{
            if(restaurantRepository.existsById(id)){
                restaurantRepository.deleteById(id);
                return APIResponse.success(null,"Restaurant deleted successfully",requestId,timestamp,HttpStatus.OK);
            }else {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Invalid restaurant id","restaurantId");
            }
        }catch (ApplicationException e){
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<APIResponse<List<Restaurant>>> getAllRestaurant(String requestId) {

        String timestamp = dateTimeUtil.getDateTime();

        try {
            List<Restaurant> restaurantList = restaurantRepository.findAll();
            if(!restaurantList.isEmpty()){
                return APIResponse.success(restaurantList,"Restaurants fetched successfully",requestId,timestamp,HttpStatus.OK);
            }else {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Restaurants not available","restaurant");
            }
        }catch (ApplicationException e){
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<APIResponse<Map<String, Boolean>>> checkExistence(String requestId, Long restaurantId) {

        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();
        try {
            if(restaurantRepository.existsById(restaurantId)){
                Map<String, Boolean> map = new HashMap<>();
                map.put("exists", true);
                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("CHECK_RESTAURANT_EXISTENCE")
                        .interactionType("API_CALL")
                        .status("BUSINESS_SUCCESS")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);
                return APIResponse.success(map,"Restaurant exists",requestId,timestamp,HttpStatus.OK);
            }else {
                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("CHECK_RESTAURANT_EXISTENCE")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Restaurants not available")
                        .timestamp(timestamp)
                        .build();
                businessEventLoggingService.saveBusinessEvent(eventLog);
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Restaurants not available","restaurant");
            }
        }catch (ApplicationException e){
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("CHECK_RESTAURANT_EXISTENCE")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();
            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        }catch (Exception e){
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("CHECK_RESTAURANT_EXISTENCE")
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
