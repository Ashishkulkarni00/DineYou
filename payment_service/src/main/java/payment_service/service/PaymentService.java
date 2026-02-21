package payment_service.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import payment_service.dto.*;
import payment_service.enums.ErrorCode;
import payment_service.enums.PaymentStatus;
import payment_service.exception.ApplicationException;
import payment_service.model.BusinessEventLog;
import payment_service.model.Payment;
import payment_service.model.PaymentEvents;
import payment_service.repository.EventRepository;
import payment_service.repository.PaymentRepository;
import payment_service.util.DateTimeUtil;
import payment_service.util.RequestInfoProvider;

import java.util.*;

@Service
public class PaymentService {

    @Value("${stripe.publishable.key}")
    private String publishableKey;

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${stripe.payment.successUrl}")
    private String paymentSuccessUrl;

    @Value("${stripe.payment.failedUrl}")
    private String paymentFailedUrl;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestaurantServiceClient restaurantServiceClient;

    @Autowired
    private OrderServiceClient orderServiceClient;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BusinessEventLoggingService businessEventLoggingService;


    @Autowired
    private RequestInfoProvider requestInfoProvider;

    public ResponseEntity<APIResponse<StripeResDto>> initializePayment(PaymentReqDto paymentReqDto, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();

        try {

            if (!restaurantServiceClient.doesRestaurantExist(paymentReqDto.getRestaurantId(), requestId)) {

                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("INITIALIZE_PAYMENT")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Invalid restaurant ID provided")
                        .timestamp(timestamp)
                        .build();

                businessEventLoggingService.saveBusinessEvent(eventLog);

                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid restaurant ID provided",
                        "restaurantId");
            }

            if (!orderServiceClient.doesOrdersExist(paymentReqDto.getOrderIds(), requestId)) {

                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("INITIALIZE_PAYMENT")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Invalid order ID provided")
                        .timestamp(timestamp)
                        .build();

                businessEventLoggingService.saveBusinessEvent(eventLog);
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid order ID provided", "orderId");
            }

            Stripe.apiKey = secretKey;

            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData
                    .builder()
                    .setName("TWO STORIES").build();

            double amount = paymentReqDto.getAmount();
            long unitAmountInMinor = Math.round(amount * 100);

            String paymentReferenceId = "ref_" + UUID.randomUUID();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(
                            paymentReqDto.getCurrency() == null ? "inr" : paymentReqDto.getCurrency().toLowerCase())
                    .setUnitAmount(unitAmountInMinor)
                    .setProductData(productData)
                    .build();

            SessionCreateParams.LineItem lineItemData = SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(priceData)
                    .build();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .putMetadata("refPaymentId", paymentReferenceId)
                    .setPaymentIntentData(
                            SessionCreateParams.PaymentIntentData.builder()
                                    .putMetadata("refPaymentId", paymentReferenceId)
                                    .build())
                    .setSuccessUrl(paymentSuccessUrl + "?paymentReferenceId=" + paymentReferenceId)
                    .setCancelUrl(paymentFailedUrl + "?paymentReferenceId=" + paymentReferenceId)
                    .addLineItem(lineItemData)
                    .build();

            Session session = Session.create(params);

            List<Payment> paymentList = new ArrayList<>();

            for(Long orderId : paymentReqDto.getOrderIds()){
                Payment payment = new Payment();
                payment.setPaymentReferenceId(paymentReferenceId);
                payment.setGatewaySessionUrl(session.getUrl());
                payment.setInitialisedAt(timestamp);
                payment.setGatewaySessionId(session.getId());
                payment.setCurrency(paymentReqDto.getCurrency());
                payment.setAmount(paymentReqDto.getAmount());
                payment.setKeycloakSessionId(requestInfoProvider.getKeycloakSessionId());
                payment.setAnonymousSessionId(requestInfoProvider.getAnonymousSessionId());
                payment.setUserId(paymentReqDto.getUserId());
                payment.setGatewayName(paymentReqDto.getGatewayName());
                payment.setOrderId(orderId);
                payment.setRestaurantId(paymentReqDto.getRestaurantId());
                payment.setPaymentMethod("UPI");
                payment.setStatus(PaymentStatus.INITIALIZED);
                paymentList.add(payment);
            }

            savePaymentInfo(paymentList);

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("INITIALIZE_PAYMENT")
                    .interactionType("API_CALL")
                    .status("BUSINESS_SUCCESS")
                    .timestamp(timestamp)
                    .build();

            businessEventLoggingService.saveBusinessEvent(eventLog);

            return APIResponse.success(
                    StripeResDto.builder()
                            .sessionId(session.getId())
                            .sessionUrl(session.getUrl())
                            .paymentAmount(paymentReqDto.getAmount())
                            .paymentReferenceId(paymentReferenceId)
                            .orderIds(paymentReqDto.getOrderIds())
                            .build(),
                    "Payment process initialised successfully",
                    requestId,
                    timestamp,
                    HttpStatus.OK);

        } catch (ApplicationException e) {

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("INITIALIZE_PAYMENT")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();

            businessEventLoggingService.saveBusinessEvent(eventLog);

            e.printStackTrace();
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("INITIALIZE_PAYMENT")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();

            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<APIResponse<CashPaymentResDto>> payInCash(PaymentReqDto paymentReqDto, String requestId) {

        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();

        try {

            if (!restaurantServiceClient.doesRestaurantExist(paymentReqDto.getRestaurantId(), requestId)) {

                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("CASH_PAYMENT")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Invalid restaurant ID provided")
                        .timestamp(timestamp)
                        .build();

                businessEventLoggingService.saveBusinessEvent(eventLog);
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid restaurant ID provided",
                        "restaurantId");
            }

            if (!orderServiceClient.doesOrdersExist(paymentReqDto.getOrderIds(), requestId)) {
                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("CASH_PAYMENT")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Invalid order ID provided")
                        .timestamp(timestamp)
                        .build();

                businessEventLoggingService.saveBusinessEvent(eventLog);
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid order ID provided", "orderId");
            }

            String paymentReferenceId = "ref_" + UUID.randomUUID();

            paymentReqDto.setGatewaySessionId("opted-cash-payment");
            paymentReqDto.setPaymentReferenceId(paymentReferenceId);
            paymentReqDto.setInitialisedAt(timestamp);
            paymentReqDto.setGatewaySessionUrl("opted-cash-payment");

            List<Payment> paymentList = new ArrayList<>();
            for(Long orderId : paymentReqDto.getOrderIds()){
                Payment payment = new Payment();
                payment.setPaymentReferenceId(paymentReferenceId);
                payment.setGatewaySessionUrl("opted-cash-payment");
                payment.setInitialisedAt(timestamp);
                payment.setGatewaySessionId("opted-cash-payment");
                payment.setCurrency(paymentReqDto.getCurrency());
                payment.setAmount(paymentReqDto.getAmount());
                payment.setKeycloakSessionId(requestInfoProvider.getKeycloakSessionId());
                payment.setAnonymousSessionId(requestInfoProvider.getAnonymousSessionId());
                payment.setUserId(paymentReqDto.getUserId());
                payment.setGatewayName(paymentReqDto.getGatewayName());
                payment.setOrderId(orderId);
                payment.setRestaurantId(paymentReqDto.getRestaurantId());
                payment.setPaymentMethod("CASH");
                payment.setStatus(PaymentStatus.SUCCESS);
                paymentList.add(payment);
            }

            savePaymentInfo(paymentList);

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("CASH_PAYMENT")
                    .interactionType("API_CALL")
                    .status("BUSINESS_SUCCESS")
                    .timestamp(timestamp)
                    .build();

            businessEventLoggingService.saveBusinessEvent(eventLog);

            return APIResponse.success(
                    CashPaymentResDto.builder()
                            .paymentAmount(paymentReqDto.getAmount())
                            .paymentReferenceId(paymentReferenceId)
                            .orderIds(paymentReqDto.getOrderIds())
                            .build(),
                    "Payment process finished successfully",
                    requestId,
                    timestamp,
                    HttpStatus.OK);

        } catch (ApplicationException e) {

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("CASH_PAYMENT")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();

            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(e.getErrorCode(), e.getMessage(), requestId, timestamp, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("CASH_PAYMENT")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();

            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private void savePaymentInfo(List<Payment> paymentList) {
        paymentRepository.saveAll(paymentList);
    }

    public void updatePaymentStatus(String paymentReferenceId, PaymentStatus paymentStatus) {
        paymentRepository.updateStatusByReferenceId(paymentReferenceId, paymentStatus, dateTimeUtil.getDateTime());
    }

    public void saveEvent(String eventId, String paymentReferenceId, String eventType, String eventData) {
        PaymentEvents paymentEvents = new PaymentEvents();
        paymentEvents.setEventId(eventId);
        paymentEvents.setPaymentReferenceId(paymentReferenceId);
        paymentEvents.setEventType(eventType);
        paymentEvents.setData(eventData);
        paymentEvents.setReceivedAt(dateTimeUtil.getDateTime());
        eventRepository.save(paymentEvents);
    }

    public ResponseEntity<APIResponse<PaymentStatusResDto>> getPaymentStatus(String paymentReferenceId,
                                                                             String requestId) {

        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();

        try {

            Optional<Payment> payment = paymentRepository.findByPaymentReferenceId(paymentReferenceId);

            if (payment.isPresent()) {
                PaymentStatusResDto resDto = new PaymentStatusResDto();
                resDto.setPaymentStatus(payment.get().getStatus());
                resDto.setPaymentReferenceId(payment.get().getPaymentReferenceId());
                resDto.setTimestamp(payment.get().getUpdatedAt());
                resDto.setOrderId(9999999999999999L);
                resDto.setPaidAmount(payment.get().getAmount());

                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("FETCH_PAYMENT_STATUS")
                        .interactionType("API_CALL")
                        .status("BUSINESS_SUCCESS")
                        .timestamp(timestamp)
                        .build();

                businessEventLoggingService.saveBusinessEvent(eventLog);

                return APIResponse.success(resDto, "Payment status fetched successfully", requestId, timestamp,
                        HttpStatus.OK);
            } else {
                BusinessEventLog eventLog = BusinessEventLog.builder()
                        .requestId(requestId)
                        .spanId(spanId)
                        .eventName("FETCH_PAYMENT_STATUS")
                        .interactionType("API_CALL")
                        .status("BUSINESS_FAILURE")
                        .errorMessage("Payment details not found")
                        .timestamp(timestamp)
                        .build();

                businessEventLoggingService.saveBusinessEvent(eventLog);
                throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND, "Payment details not found", "payment");
            }
        } catch (ApplicationException e) {
            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_PAYMENT_STATUS")
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
                    .eventName("FETCH_PAYMENT_STATUS")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();

            businessEventLoggingService.saveBusinessEvent(eventLog);
            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<APIResponse<List<PaymentStatusResDto>>> getPaymentStatusByOrderId(List<Long> orderIds, String requestId) {
        String timestamp = dateTimeUtil.getDateTime();
        String spanId = UUID.randomUUID().toString();
        try {

            List<Payment> paymentList = paymentRepository.findByOrderIdIn(orderIds);
            List<PaymentStatusResDto> responseDtoList = new ArrayList<>();
            for (Payment payment : paymentList) {
                PaymentStatusResDto resDto = new PaymentStatusResDto();
                resDto.setPaymentStatus(payment.getStatus());
                resDto.setPaymentReferenceId(payment.getPaymentReferenceId());
                resDto.setTimestamp(payment.getUpdatedAt());
                resDto.setOrderId(payment.getOrderId());
                resDto.setPaidAmount(payment.getAmount());
                responseDtoList.add(resDto);
            }

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_PAYMENT_STATUS")
                    .interactionType("API_CALL")
                    .status("BUSINESS_SUCCESS")
                    .timestamp(timestamp)
                    .build();

            businessEventLoggingService.saveBusinessEvent(eventLog);

            return APIResponse.success(responseDtoList, "Payment status fetched successfully", requestId, timestamp,
                    HttpStatus.OK);

        } catch (ApplicationException e) {

            BusinessEventLog eventLog = BusinessEventLog.builder()
                    .requestId(requestId)
                    .spanId(spanId)
                    .eventName("FETCH_PAYMENT_STATUS")
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
                    .eventName("FETCH_PAYMENT_STATUS")
                    .interactionType("API_CALL")
                    .status("BUSINESS_FAILURE")
                    .errorMessage(e.getMessage())
                    .timestamp(timestamp)
                    .build();

            businessEventLoggingService.saveBusinessEvent(eventLog);

            return APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), requestId, timestamp,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
