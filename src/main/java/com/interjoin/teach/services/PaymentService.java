package com.interjoin.teach.services;

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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private String paymentSuccessUrl = "http://localhost:3000";
    private String paymentCancelUrl = "http://localhost:3000?status=PAYMENT_DECLINED";


    public String openPaymentPage(BigDecimal price, String subject, Map<String, String> metadata, User currentUser, String urlParams) {

        Stripe.apiKey = "sk_test_51KdsJaHiAI1FpLGq7shhYXjXrm3nsK5bM9ALw6Rk8YWSa6qLR40WS6NqFnwgwby5VyGD4hZITPIYe8gFoEQSUEJD00UIyHsMpM";

        if(Optional.ofNullable(urlParams).isPresent()) {
            paymentSuccessUrl = String.format("%s%s", paymentSuccessUrl, urlParams);
        }

        Customer customer = getStripeUserByEmail(currentUser);

        SessionCreateParams params =
                SessionCreateParams.builder()

                        .setPaymentIntentData(new SessionCreateParams.PaymentIntentData.Builder()
                                .putAllMetadata(metadata)
                                .build())

                        .putAllMetadata(metadata)
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
                                                        .setUnitAmountDecimal(price.multiply(BigDecimal.valueOf(100)))
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
        params.put("email", user.getEmail());
        CustomerCollection customers =
                Customer.list(params);

        if (customers.getData().isEmpty()) {
            Map<String, Object> customerParams = new HashMap<>();
            customerParams.put("email", user.getEmail());
            customerParams.put("name", String.format("%s %s", user.getFirstName(), user.getLastName()));

            return Customer.create(customerParams);
        } else {
            return customers.getData().stream().findFirst().orElse(null);
        }
    }
}
