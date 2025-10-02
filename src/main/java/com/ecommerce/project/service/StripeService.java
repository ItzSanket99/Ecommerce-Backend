package com.ecommerce.project.service;

import com.ecommerce.project.payload.StripePaymentDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeService {
    public PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDTO) throws StripeException;
}
