package com.example.merainstitue;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merainstitue.api.CreatePaymentIntentResponse;
import com.example.merainstitue.api.PaymentIntentRequest;
import com.example.merainstitue.api.StripeService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";
    private Stripe stripe;
    private String clientSecret;
    private Button payButton;
    private CardInputWidget cardInputWidget;
    private static final String PUBLISH_KEY = "pk_test_51QeFGDF4BzOczJWmb0WrMwj7lsCs4Pe0WkfhvvpKi0ekG8EaqSaQZkxULwvLjZaOv3ApjA1XoyvVnLbX9JiTV56U00rJo601W0";
    private static final int PAYMENT_REQUEST_CODE = 1234;

    // Declare course variables to store data passed from DetailActivity
    private String courseId;
    private String courseTitle;
    private String courseSubtitle;
    private double coursePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Log.d(TAG, "Initializing payment activity");

        // Retrieve the course details passed from DetailActivity
        courseId = getIntent().getStringExtra("courseId");
        courseTitle = getIntent().getStringExtra("courseTitle");
        courseSubtitle = getIntent().getStringExtra("courseSubtitle");
        coursePrice = getIntent().getDoubleExtra("coursePrice", 0.0);

        // Initialize the views
        TextView courseTitleTextView = findViewById(R.id.courseTitle);
        TextView courseSubtitleTextView = findViewById(R.id.courseDescription);
        TextView coursePriceTextView = findViewById(R.id.coursePrice);

        // Set the retrieved course data to the UI
        courseTitleTextView.setText(courseTitle);
        courseSubtitleTextView.setText(courseSubtitle);
        coursePriceTextView.setText("$" + coursePrice);

        try {
            PaymentConfiguration.init(getApplicationContext(), PUBLISH_KEY);
            stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Stripe: ", e);
            Toast.makeText(this, "Error initializing payment system", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        payButton = findViewById(R.id.payButton);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        payButton.setEnabled(false);

        payButton.setOnClickListener(v -> {
            if (clientSecret != null) {
                initiatePayment();
            } else {
                Toast.makeText(this, "Payment not ready yet. Please wait.", Toast.LENGTH_SHORT).show();
            }
        });

        getClientSecretFromServer();
    }

    private void getClientSecretFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StripeService service = retrofit.create(StripeService.class);

        // Use the course price and currency in the request
        PaymentIntentRequest request = new PaymentIntentRequest((int) (coursePrice * 100), "usd");  // Price is in cents for Stripe
        service.createPaymentIntent(request).enqueue(new Callback<CreatePaymentIntentResponse>() {
            @Override
            public void onResponse(Call<CreatePaymentIntentResponse> call, Response<CreatePaymentIntentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    clientSecret = response.body().getClientSecret();
                    payButton.setEnabled(true);
                    Toast.makeText(PaymentActivity.this, "Ready for payment", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "Failed to initialize payment. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreatePaymentIntentResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Network error occurred. Please check your connection.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initiatePayment() {
        PaymentMethodCreateParams.Card card = cardInputWidget.getPaymentMethodCard();
        if (card != null) {
            payButton.setEnabled(false);

            PaymentMethodCreateParams paymentMethodParams = PaymentMethodCreateParams.create(card);
            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(paymentMethodParams, clientSecret);

            try {
                stripe.confirmPayment(this, confirmParams);
            } catch (Exception e) {
                Toast.makeText(this, "Payment processing error", Toast.LENGTH_LONG).show();
                payButton.setEnabled(true);
            }
        } else {
            Toast.makeText(this, "Please enter valid card details", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Save data to Firebase and send notification
                savePurchaseToFirebase();

                // Redirect to the lesson page
                Intent lessonIntent = new Intent(this, DetailActivity.class);
                lessonIntent.putExtra("courseId", courseId);  // Pass course details
                lessonIntent.putExtra("courseTitle", courseTitle);
                startActivity(lessonIntent);

                Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show();
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_LONG).show();
                payButton.setEnabled(true);
            } else {
                Toast.makeText(this, "Payment Failed - Please try again", Toast.LENGTH_LONG).show();
                payButton.setEnabled(true);
            }
        }

    }

    private void savePurchaseToFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> purchaseDetails = new HashMap<>();
        purchaseDetails.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        purchaseDetails.put("courseId", courseId);
        purchaseDetails.put("courseTitle", courseTitle);
        purchaseDetails.put("courseSubtitle", courseSubtitle);
        purchaseDetails.put("coursePrice", coursePrice);
        purchaseDetails.put("purchaseDate", System.currentTimeMillis());

        db.collection("userCourses")
                .add(purchaseDetails)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "Purchase details added to Firebase with ID: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error adding purchase details to Firebase: ", e));
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

