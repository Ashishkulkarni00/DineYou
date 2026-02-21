package payment_service.controller;

import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.model.checkout.Session;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payment_service.enums.PaymentStatus;
import payment_service.service.PaymentService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/payment")
public class StripeWebhookController {

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    public StripeWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request) throws IOException {

        String payload = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        String sigHeader = request.getHeader("Stripe-Signature");

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
        } catch (JsonSyntaxException | SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        switch (event.getType()) {

            case "checkout.session.completed": {
                Session session = (Session) event.getData().getObject();
                String paymentReferenceId = session.getMetadata().get("refPaymentId");
                String eventId = event.getId();
                String eventType = event.getType();
                paymentService.saveEvent(eventId, paymentReferenceId, eventType, session.toString());
                paymentService.updatePaymentStatus(paymentReferenceId, PaymentStatus.SUCCESS);
                break;
            }

            case "payment_intent.created": {
                PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
                String paymentReferenceId = paymentIntent.getMetadata().get("refPaymentId");
                String eventId = event.getId();
                String eventType = event.getType();
                paymentService.saveEvent(eventId, paymentReferenceId, eventType, paymentIntent.toString());
                break;
            }

            case "payment_intent.succeeded": {
                PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
                String paymentReferenceId = paymentIntent.getMetadata().get("refPaymentId");
                String eventId = event.getId();
                String eventType = event.getType();
                paymentService.saveEvent(eventId, paymentReferenceId, eventType, paymentIntent.toString());
                break;
            }

            case "payment_intent.payment_failed": {
                PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
                String paymentReferenceId = paymentIntent.getMetadata().get("refPaymentId");
                String eventId = event.getId();
                String eventType = event.getType();
                paymentService.saveEvent(eventId, paymentReferenceId, eventType, paymentIntent.toString());
                paymentService.updatePaymentStatus(paymentReferenceId, PaymentStatus.FAILED);
                break;
            }

            case "charge.succeeded" : {

                Charge charge = (Charge) event.getData().getObject();
                String eventId = event.getId();
                String eventType = event.getType();
                String paymentReferenceId = charge.getMetadata().get("refPaymentId");
                paymentService.saveEvent(eventId, paymentReferenceId, eventType, charge.toString());
                break;

            }

            case "charge.updated" : {

                Charge charge = (Charge) event.getData().getObject();
                String eventId = event.getId();
                String eventType = event.getType();
                String paymentReferenceId = charge.getMetadata().get("refPaymentId");

                paymentService.saveEvent(eventId, paymentReferenceId, eventType, charge.toString());
                break;

            }

            default:
                System.out.println("Unhandled Stripe event type: " + event.getType());
                break;
        }

        return ResponseEntity.ok("");
    }



}
