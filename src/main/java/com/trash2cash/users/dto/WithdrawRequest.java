package com.trash2cash.users.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {
    private BigDecimal amount;
    private String bankCode;
    private String accountNumber;
}
