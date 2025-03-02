package com.example.merainstitue.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface StripeService {

    @POST("/create-payment-intent")
    Call<CreatePaymentIntentResponse> createPaymentIntent(@Body PaymentIntentRequest request);
}
