package payment_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payment_service.dto.*;
import payment_service.service.PaymentService;

import java.util.List;

@RequestMapping("/api/v1/payment")
@RestController
@CrossOrigin("*")
public class PaymentController {


    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initialize")
    public ResponseEntity<APIResponse<StripeResDto>> initializePayment(
            @RequestBody PaymentReqDto paymentReqDto,
            HttpServletRequest request
    ) {
        String requestId = (String) request.getAttribute("requestId");
        return paymentService.initializePayment(paymentReqDto, requestId);
    }


    @PostMapping("/pay-in-cash")
    public ResponseEntity<APIResponse<CashPaymentResDto>> payInCash(
            @RequestBody PaymentReqDto paymentReqDto,
            HttpServletRequest request
    ) {
        String requestId = (String) request.getAttribute("requestId");
        return paymentService.payInCash(paymentReqDto, requestId);
    }


    @GetMapping("/getPaymentStatus/{paymentReferenceId}")
    public ResponseEntity<APIResponse<PaymentStatusResDto>> getPaymentStatus(
            @PathVariable("paymentReferenceId") String paymentReferenceId,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return paymentService.getPaymentStatus(paymentReferenceId, requestId);
    }

    @GetMapping("/getPaymentStatus/by-order-ids/{orderIds}")
    public ResponseEntity<APIResponse<List<PaymentStatusResDto>>> getPaymentStatusByOrderId(
            @PathVariable("orderIds") List<Long> orderIds,
            HttpServletRequest request
    ){
        String requestId = (String) request.getAttribute("requestId");
        return paymentService.getPaymentStatusByOrderId(orderIds, requestId);
    }   

}
