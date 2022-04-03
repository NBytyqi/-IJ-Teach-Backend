package com.interjoin.teach.services;

import com.interjoin.teach.dtos.requests.BookSessionRequest;
import com.interjoin.teach.entities.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserService userService;
    private final String paymentSuccessUrl = "http://localhost:3000";
    private final String paymentCancelUrl = "http://localhost:3000";

    public String openPaymentPage(BookSessionRequest sessionRequest, Long sessionId, BigDecimal price, String studentName, String teacherName, String subjectName, String curriculum) {

        Stripe.apiKey = "sk_test_51KdsJaHiAI1FpLGq7shhYXjXrm3nsK5bM9ALw6Rk8YWSa6qLR40WS6NqFnwgwby5VyGD4hZITPIYe8gFoEQSUEJD00UIyHsMpM";

        User currentUser = userService.getCurrentUserDetails();

        Customer customer = getStripeUserByEmail(currentUser);

        Map<String, String> datasetMetadata= new HashMap<>();
        datasetMetadata.put("sessionId", String.valueOf(sessionId));

        final String subject = String.format("Session between %s and %s on subject: \"%s\" and Curriculum: \"%s\"", studentName, teacherName, subjectName, curriculum);

        SessionCreateParams params =
                SessionCreateParams.builder()

                        .setPaymentIntentData(new SessionCreateParams.PaymentIntentData.Builder()
                                .putAllMetadata(datasetMetadata)
                                .build())

                        .putAllMetadata(datasetMetadata)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(paymentSuccessUrl)
//                        .setCancelUrl(paymentCancelUrl + "/" + paymentRequestDto.getDatasetUuid() + "/edit")
                        .setCancelUrl(paymentCancelUrl)
                        .setCustomer(customer.getId())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("usd")
                                                        .setUnitAmountDecimal(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(100)))
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName(subject)
                                                                        .build())
                                                        .build())
                                        .build())
                        .build();


        Session session = null;
        try {

            session = Session.create(params);

        } catch (StripeException e) {
            e.printStackTrace();
        }

        return session.getUrl();

    }

    @SneakyThrows
    public Customer getStripeUserByEmail(User user){

        Map<String, Object> params = new HashMap<>();
        params.put("email", "bytyqinderim87@gmail.com");
        CustomerCollection customers =
                Customer.list(params);

        if (customers.getData().isEmpty()) {
            Map<String, Object> customerParams = new HashMap<>();
            customerParams.put("email", "bytyqinderim87@gmail.com");
            customerParams.put("name", "John" + " " + "Smith");

            return Customer.create(customerParams);
        } else {
            return customers.getData().stream().findFirst().orElse(null);
        }
    }
}
