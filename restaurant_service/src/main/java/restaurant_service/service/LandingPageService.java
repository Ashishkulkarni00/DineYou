package restaurant_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import restaurant_service.dto.APIResponse;
import restaurant_service.dto.LandingPageResDto;
import restaurant_service.dto.MenuItemResDto;
import restaurant_service.enums.ErrorCode;
import restaurant_service.exception.ApplicationException;
import restaurant_service.model.BusinessEventLog;
import restaurant_service.model.LandingPage;
import restaurant_service.repository.LandingPageRepository;
import restaurant_service.util.DateTimeUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LandingPageService {

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private LandingPageRepository landingPageRepository;

    @Autowired
    MenucardServiceClient menucardServiceClient;

    @Autowired
    BusinessEventLoggingService businessEventLoggingService;

    public ResponseEntity<APIResponse<LandingPageResDto>> getLandingPageDetails(
            String requestId,
            Long restaurantId) {

        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();

        try {
            // 1. Fetch all active landing page banners
            List<LandingPage> landingPages =
                    landingPageRepository.findAllByRestaurant_RestaurantIdAndActiveTrue(restaurantId);

            if (landingPages == null || landingPages.isEmpty()) {
                throw new ApplicationException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Landing page details not found for restaurant",
                        "landingPage");
            }

            // 2. Use first record for restaurant-level info
            LandingPage firstLandingPage = landingPages.get(0);

            // 3. Popular items
            List<MenuItemResDto> popularItems =
                    menucardServiceClient.getPopularItems(restaurantId, requestId);

            if (popularItems == null || popularItems.isEmpty()) {
                throw new ApplicationException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Popular items not found",
                        "popularItems");
            }

            // 4. Convert popular item image paths
//            popularItems.forEach(item ->
//                    item.setImagePath(toPublicUrl(item.getImagePath()))
//            );

            // 5. Map landing pages → banner DTOs
            List<LandingPageResDto.BannerDto> bannerDtos =
                    landingPages.stream()
                            .map(lp -> new LandingPageResDto.BannerDto(
                                    lp.getLandingPageId(),
                                    lp.getTitle(),
                                    lp.getDescription(),
                                    lp.getDiscountPercentage(),
                                    lp.getRating(),
                                    lp.getReviewCount(),
                                    toPublicUrl(lp.getImagePath()),
                                    lp.getSortOrder(),
                                    lp.getActive()))
                            .sorted(Comparator.comparing(
                                    LandingPageResDto.BannerDto::getSortOrder,
                                    Comparator.nullsLast(Integer::compareTo)))
                            .collect(Collectors.toList());

            // 6. Build response DTO
            LandingPageResDto response = new LandingPageResDto();
            response.setRestaurantId(firstLandingPage.getRestaurant().getRestaurantId());
            response.setRestaurantName(firstLandingPage.getRestaurant().getName());
            response.setLocation(firstLandingPage.getRestaurant().getCity());
            response.setAddress(firstLandingPage.getRestaurant().getAddressLine1());
            response.setAverageRating(firstLandingPage.getRating());
            response.setTotalReviews(firstLandingPage.getReviewCount());
            response.setLogoImagePath(
                    toPublicUrl(firstLandingPage.getRestaurant().getGetImagePath()));
            response.setBanners(bannerDtos);
            response.setPopularItems(popularItems);
            response.setLastUpdated(timestamp);

            // 7. Business log
            businessEventLoggingService.saveBusinessEvent(
                    BusinessEventLog.builder()
                            .requestId(requestId)
                            .spanId(spanId)
                            .eventName("FETCH_LANDING_PAGE")
                            .interactionType("API_CALL")
                            .status("BUSINESS_SUCCESS")
                            .timestamp(timestamp)
                            .build());

            return APIResponse.success(
                    response,
                    "Landing page details fetched successfully",
                    requestId,
                    timestamp,
                    HttpStatus.OK);

        } catch (ApplicationException e) {

            businessEventLoggingService.saveBusinessEvent(
                    BusinessEventLog.builder()
                            .requestId(requestId)
                            .spanId(spanId)
                            .eventName("FETCH_LANDING_PAGE")
                            .interactionType("API_CALL")
                            .status("BUSINESS_FAILURE")
                            .errorMessage(e.getMessage())
                            .timestamp(timestamp)
                            .build());

            return APIResponse.error(
                    e.getErrorCode(),
                    e.getMessage(),
                    requestId,
                    timestamp,
                    HttpStatus.NOT_FOUND);

        } catch (Exception e) {

            businessEventLoggingService.saveBusinessEvent(
                    BusinessEventLog.builder()
                            .requestId(requestId)
                            .spanId(spanId)
                            .eventName("FETCH_LANDING_PAGE")
                            .interactionType("API_CALL")
                            .status("BUSINESS_FAILURE")
                            .errorMessage(e.getMessage())
                            .timestamp(timestamp)
                            .build());

            return APIResponse.error(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    requestId,
                    timestamp,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private String toPublicUrl(String path) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(path)
                .toUriString();
    }


}

