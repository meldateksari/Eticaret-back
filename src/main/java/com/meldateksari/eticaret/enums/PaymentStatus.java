package com.meldateksari.eticaret.enums;

public enum PaymentStatus {
    PAID,
    PENDING,
    FAILED,
    REFUNDED;

    public static PaymentStatus from(String s) {
        if (s == null) {
            throw new IllegalArgumentException("paymentStatus is null");
        }
        return PaymentStatus.valueOf(s.trim().toUpperCase());
    }
}


