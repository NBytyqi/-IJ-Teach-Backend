package com.interjoin.teach.controllers;

import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.entities.Session;
import com.interjoin.teach.enums.SessionStatus;
import com.interjoin.teach.services.SessionService;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripePaymentController {
    private final SessionService sessionService;

    @PostMapping("success-webhook")
    public ResponseEntity<String> handleSuccessfulPayment(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws NoSuchAlgorithmException, InvalidKeyException, InterjoinException {

        String endpointSecret = "whsec_84XTv2ZwkfSCwRTqYotUZkNF4IIiETPW";
        Event event;



        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            System.out.println("Failed signature verification");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
        }


        switch (event.getType()) {
            case "charge.succeeded": {

//                final String CHARGE_ID = JsonPath.read(payload, "data.object.id");
//                System.out.println("Nderim charge" + CHARGE_ID);
//                System.out.println(payload);

                final String SESSION_ID = JsonPath.read(payload, "data.object.metadata.sessionId");
//                System.out.println("I read the dataset uuid");
                final String CHARGE_ID = JsonPath.read(payload, "data.object.id");
                final String PAYMENT_INTENT_ID = JsonPath.read(payload, "data.object.payment_intent");

                Session session = sessionService.findById(Long.valueOf(SESSION_ID));
                session.setChargeId(CHARGE_ID);
                session.setPaymentIntent(PAYMENT_INTENT_ID);
                session.setSessionStatus(SessionStatus.PENDING_APPROVAL);
                sessionService.update(session);
                System.out.println("Updating " + session);

            }

            case "checkout.session.completed":

                // sessionId in case there is a session
                // teacherId in case there is an interjoin verification process
                List<String> metadataToCheck = Arrays.asList("sessionId", "teacherId");
                for(String metadata : metadataToCheck) {
                    System.out.println("Continuuuing");
                    try {
                        String metadataValue = JsonPath.read(payload, "data.object.metadata." + metadata);
                        System.out.println("Nderim session" + metadataValue);
                        System.out.println();
                        System.out.println(payload);


                    } catch (PathNotFoundException ex) {
                        System.out.println(ex);
                    }
                }

                break;
            default:
                // Unexpected event type
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
