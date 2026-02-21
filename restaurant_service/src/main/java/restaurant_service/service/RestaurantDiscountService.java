package restaurant_service.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import restaurant_service.dto.APIResponse;
import restaurant_service.dto.RestaurantDiscountReqDto;
import restaurant_service.enums.ErrorCode;
import restaurant_service.exception.ApplicationException;
import restaurant_service.model.Restaurant;
import restaurant_service.model.RestaurantDiscount;
import restaurant_service.repository.RestaurantDiscountRepository;
import restaurant_service.repository.RestaurantRepository;
import restaurant_service.util.DateTimeUtil;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantDiscountService {

    @Autowired
    private RestaurantDiscountRepository restaurantDiscountRepository;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ModelMapper modelMapper;


    public ResponseEntity<APIResponse<RestaurantDiscount>> createDiscount(RestaurantDiscountReqDto restaurantDiscountReqDto, String requestId) {
        try{
            if(restaurantDiscountReqDto.getRestaurantId() == null){
                throw new ApplicationException(ErrorCode.MISSING_REQUIRED_FIELD, "Please provide restaurant id", "restaurantId");
            }else if(!restaurantRepository.existsById(restaurantDiscountReqDto.getRestaurantId())){
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Invalid restaurant id","restaurantId");
            }else{
                Optional<Restaurant> existingRestaurant = restaurantRepository.findById(restaurantDiscountReqDto.getRestaurantId());
                if(existingRestaurant.isPresent()){
                    RestaurantDiscount discount = new RestaurantDiscount();
                    modelMapper.map(restaurantDiscountReqDto, discount);
                    discount.setRestaurant(existingRestaurant.get());
                    discount = restaurantDiscountRepository.save(discount);
                    return APIResponse.success(discount,"Discount added for restaurant", requestId, dateTimeUtil.getDateTime(), HttpStatus.CREATED);
                }else {
                    throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Invalid restaurant id","restaurantId");
                }
            }
        }catch (ApplicationException e){
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, dateTimeUtil.getDateTime(), HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, dateTimeUtil.getDateTime(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<APIResponse<RestaurantDiscount>> getDiscount(Long id, String requestId){
        try{
            if(restaurantDiscountRepository.existsById(id)){
                Optional<RestaurantDiscount> existingDiscount = restaurantDiscountRepository.findById(id);
                return APIResponse.success(existingDiscount.get(),"Restaurant discount fetched successfully",requestId,dateTimeUtil.getDateTime(),HttpStatus.OK);
            }else {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid restaurant discount id", "restaurantDiscountId");
            }
        }catch (ApplicationException e){
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, dateTimeUtil.getDateTime(), HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, dateTimeUtil.getDateTime(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<APIResponse<RestaurantDiscount>> updateDiscount(Long discountId,RestaurantDiscountReqDto reqDto, String requestId){
        try{
                Optional<RestaurantDiscount> existingDiscount = restaurantDiscountRepository.findById(discountId);
            if(existingDiscount.isPresent()){
                modelMapper.map(reqDto,existingDiscount.get());
                RestaurantDiscount discount = restaurantDiscountRepository.save(existingDiscount.get());
                return APIResponse.success(discount,"Restaurant discount updated successfully",requestId,dateTimeUtil.getDateTime(),HttpStatus.OK);
            }else {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Invalid restaurant discount id", "restaurantDiscountId");
            }
        }catch (ApplicationException e){
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, dateTimeUtil.getDateTime(), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, dateTimeUtil.getDateTime(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<APIResponse<Void>> deleteDiscount(Long id, String requestId) {
        try {
            if(restaurantDiscountRepository.existsById(id)){
                restaurantDiscountRepository.deleteById(id);
                return APIResponse.success(null,"Restaurant discount deleted successfully",requestId,dateTimeUtil.getDateTime(),HttpStatus.OK);
            }else {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,"Invalid restaurant discount id", "restaurantDiscountId");
            }
        }catch (ApplicationException e){
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, dateTimeUtil.getDateTime(), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, dateTimeUtil.getDateTime(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<APIResponse<List<RestaurantDiscount>>> getDiscountsByRestaurantId(Long restaurantId, String requestId) {
        try {
            if(restaurantRepository.existsById(restaurantId)){
                List<RestaurantDiscount> restaurantDiscounts = restaurantDiscountRepository.findDiscountsByRestaurantId(restaurantId);
                if(restaurantDiscounts.isEmpty()){
                    throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "No discounts found", "restaurantId");
                }else {
                    return APIResponse.success(restaurantDiscounts,"Restaurant discounts fetched successfully",requestId,dateTimeUtil.getDateTime(),HttpStatus.OK);
                }
            }else {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid restaurant id","restaurantId");
            }
        }catch (ApplicationException e){
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, dateTimeUtil.getDateTime(), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, dateTimeUtil.getDateTime(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<APIResponse<List<RestaurantDiscount>>> getActiveDiscountsByRestaurantId(Long restaurantId, String requestId) {
        try {
            if(restaurantRepository.existsById(restaurantId)){
                List<RestaurantDiscount> restaurantDiscounts = restaurantDiscountRepository.findActiveDiscountsByRestaurantId(restaurantId);
                if(restaurantDiscounts.isEmpty()){
                    throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "No discounts found", "restaurantId");
                }else {
                    return APIResponse.success(restaurantDiscounts,"Restaurant discounts fetched successfully",requestId,dateTimeUtil.getDateTime(),HttpStatus.OK);
                }
            }else {
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid restaurant id","restaurantId");
            }
        }catch (ApplicationException e){
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, dateTimeUtil.getDateTime(), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, dateTimeUtil.getDateTime(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

