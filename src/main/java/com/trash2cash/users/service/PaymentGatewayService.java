package com.trash2cash.users.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentGatewayService {
    public boolean processWithdrawal(BigDecimal amount, String bankCode, String accountNumber) {
        // TODO: Integrate with Paystack/Flutterwave
        System.out.println("Processing payout to account " + accountNumber + " at bank " + bankCode);
        return true;
    }
}
