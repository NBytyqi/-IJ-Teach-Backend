package com.interjoin.teach.controllers;

import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.dtos.EmailDTO;
import com.interjoin.teach.entities.Session;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.enums.SessionStatus;
import com.interjoin.teach.services.EmailService;
import com.interjoin.teach.services.SessionService;
import com.interjoin.teach.services.UserService;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripePaymentController {
    private final SessionService sessionService;
    private final UserService userService;
    private final EmailService emailService;

    @Value("${spring.sendgrid.templates.session-payment-confirmation}")
    private String paymentConfirmationTemplate;
    @Value("${spring.sendgrid.templates.session-request-template}")
    private String sessionRequestTemplate;

    private final String FIRST_NAME = "firstName";

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
                String SESSION_ID = null;
                String INTERJOIN_VERIFICATION_TEACHER_ID = null;
                try {
                    SESSION_ID = JsonPath.read(payload, "data.object.metadata.sessionId");
                    INTERJOIN_VERIFICATION_TEACHER_ID = JsonPath.read(payload, "data.object.metadata.teacherIdForVerification");
                } catch (Exception e) {
                    System.out.println("Exception is thrown there " + e.getMessage());
                }
//                System.out.println("I read the dataset uuid");
                final String CHARGE_ID = JsonPath.read(payload, "data.object.id");
                final String PAYMENT_INTENT_ID = JsonPath.read(payload, "data.object.payment_intent");
                if(Optional.ofNullable(SESSION_ID).isPresent()) {
                    finishPaymentForSession(SESSION_ID, CHARGE_ID, PAYMENT_INTENT_ID);
                } else if(Optional.ofNullable(INTERJOIN_VERIFICATION_TEACHER_ID).isPresent()) {
                    finishPaymentForPurchaseVerification(INTERJOIN_VERIFICATION_TEACHER_ID, CHARGE_ID, PAYMENT_INTENT_ID);
                }

            }

            case "checkout.session.completed":

                // sessionId in case there is a session
                // teacherId in case there is an interjoin verification process
//                List<String> metadataToCheck = Arrays.asList("sessionId", "teacherId");
//                for(String metadata : metadataToCheck) {
//                    System.out.println("Continuuuing");
//                    try {
//                        String metadataValue = JsonPath.read(payload, "data.object.metadata." + metadata);
//                        System.out.println("Nderim session" + metadataValue);
//                        System.out.println();
//                        System.out.println(payload);
//
//
//                    } catch (PathNotFoundException ex) {
//                        System.out.println(ex);
//                    }
//                }

                break;
            default:
                // Unexpected event type
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @Value("${spring.sendgrid.templates.purchase-verification-template}")
    private String purchaseVerificationTemplate;

    private void finishPaymentForPurchaseVerification(String teacherUuid, String chargeId, String paymentIntentId) {
        User teacher = userService.findByUuid(teacherUuid);
        teacher.setPurchasedVerification(true);
        userService.save(teacher);
        Map<String, String> templateKeys= new HashMap<>();
        templateKeys.put(FIRST_NAME, teacher.getFirstName());
        EmailDTO emailDTO = EmailDTO.builder()
                .toEmail(teacher.getEmail())
                .templateId(purchaseVerificationTemplate)
                .templateKeys(templateKeys)
                .build();
        emailService.sendEmail(emailDTO);
    }

    public void finishPaymentForSession(String sessionId, String chargeId, String paymentIntentId) throws InterjoinException {
        Session session = sessionService.findById(Long.valueOf(sessionId));
        session.setChargeId(chargeId);
        session.setPaymentIntent(paymentIntentId);
        session.setSessionStatus(SessionStatus.PENDING_APPROVAL);
        sessionService.update(session);
        System.out.println("Updating " + session);

        //send payment confirmation email to student
        Map<String, String> templateKeysForStudent = new HashMap<>();
        User student = session.getStudent();
        templateKeysForStudent.put(FIRST_NAME, student.getFirstName());
        EmailDTO emailDTO = EmailDTO.builder()
                .toEmail(student.getEmail())
                .templateId(paymentConfirmationTemplate)
                .templateKeys(templateKeysForStudent)
                .build();
        emailService.sendEmail(emailDTO);

        // send email for session request to teacher
        Map<String, String> templateKeysForTeacher = new HashMap<>();
        User teacher = session.getTeacher();
        templateKeysForTeacher.put(FIRST_NAME, teacher.getFirstName());
        EmailDTO emailDTOTeacher = EmailDTO.builder()
                .toEmail(teacher.getEmail())
                .templateId(sessionRequestTemplate)
                .templateKeys(templateKeysForTeacher)
                .build();
        emailService.sendEmail(emailDTOTeacher);
    }
}
