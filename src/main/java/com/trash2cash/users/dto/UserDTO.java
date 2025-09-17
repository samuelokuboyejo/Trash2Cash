package com.trash2cash.users.dto;

import com.trash2cash.wallet.WalletDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private WalletDTO wallet;
}
