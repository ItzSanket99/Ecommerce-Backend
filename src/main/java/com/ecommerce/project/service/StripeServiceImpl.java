package com.ecommerce.project.service;

import com.ecommerce.project.payload.StripePaymentDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDTO) throws StripeException {
        Customer customer;
        CustomerSearchParams searchParams =
                    CustomerSearchParams.builder()
                            .setQuery("email:'" + stripePaymentDTO.getEmail() + "'")
                            .build();
            CustomerSearchResult customers = Customer.search(searchParams);
        if(customers.getData().isEmpty()){
            CustomerCreateParams customerParams =
                    CustomerCreateParams.builder()
                            .setName(stripePaymentDTO.getName())
                            .setEmail(stripePaymentDTO.getEmail())
                            .setAddress(
                                    CustomerCreateParams.Address.builder()
                                            .setLine1(stripePaymentDTO.getAddress().getStreet())
                                            .setCity(stripePaymentDTO.getAddress().getCity())
                                            .setState(stripePaymentDTO.getAddress().getState())
                                            .setPostalCode(stripePaymentDTO.getAddress().getPincode())
                                            .setCountry(stripePaymentDTO.getAddress().getCountry())
                                            .build()
                            )
                            .build();
            customer = Customer.create(customerParams);
        }else{
            customer = customers.getData().getFirst();
        }
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(stripePaymentDTO.getAmount())
                        .setCurrency(stripePaymentDTO.getCurrency())
                        .setCustomer(customer.getId())
                        .setDescription(stripePaymentDTO.getDescription())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        return PaymentIntent.create(params);
    }
}
