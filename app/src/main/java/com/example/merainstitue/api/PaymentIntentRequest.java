package com.example.merainstitue.api;

public class PaymentIntentRequest {
    private long amount;
    private String currency;

    public PaymentIntentRequest(long amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
