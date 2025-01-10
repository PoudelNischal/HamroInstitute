package com.example.merainstitue.api;

import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentIntentConfirmParams;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.Stripe;

public class PaymentResultCallback implements PaymentResultCallback {

    @Override
    public void onSuccess(PaymentIntent paymentIntent) {
        // Handle success, process the result and take action like saving purchase info to Firebase
        Toast.makeText(context, "Payment Successful!", Toast.LENGTH_LONG).show();
        // Call your success actions, like moving to next screen or saving to Firebase
    }

    @Override
    public void onError(Exception e) {
        // Handle error, show user feedback
        Toast.makeText(context, "Payment Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
