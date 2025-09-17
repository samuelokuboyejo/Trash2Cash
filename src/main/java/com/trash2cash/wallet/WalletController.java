package com.trash2cash.wallet;

import com.trash2cash.transactions.TransactionDto;
import com.trash2cash.users.dto.WalletDto;
import com.trash2cash.users.dto.WithdrawRequest;
import com.trash2cash.transactions.Transaction;
import com.trash2cash.users.model.UserInfoUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallets")
@Tag(name = "Wallet API", description = "Manage user wallet: get balance, deposit, withdraw, add points")
public class WalletController {

    private final WalletService walletService;

    @Operation(
            summary = "Get User Wallet",
            description = "Fetches the wallet details (balance, user info, etc.) for a given user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Wallet.class))),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWallet(
            @Parameter(description = "ID of the user", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getUserWallet(userId));
    }

    @Operation(
            summary = "Deposit into Wallet",
            description = "Adds funds to the user’s wallet."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deposit successful",
                    content = @Content(schema = @Schema(implementation = Wallet.class))),
            @ApiResponse(responseCode = "400", description = "Invalid deposit request")
    })
    @PostMapping("/{userId}/deposit")
    public ResponseEntity<WalletDto> deposit(
            @Parameter(description = "ID of the user", example = "1") @PathVariable Long userId,
            @Parameter(description = "Amount to deposit", example = "1000.50") @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(walletService.deposit(userId, amount));
    }

    @Operation(summary = "Withdraw funds", description = "Processes withdrawal request via Paystack/Flutterwave")
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionDto> withdraw(@RequestBody WithdrawRequest request, @AuthenticationPrincipal UserInfoUserDetails principal) {
        String email = principal.getUsername();
        walletService.withdraw(request, email);
        return  ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Add reward points to user’s wallet",
            description = "This endpoint allows you to add reward points to a user’s wallet by specifying the userId and the number of points."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Points successfully added",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Wallet.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g. negative points)"),
            @ApiResponse(responseCode = "404", description = "Wallet or User not found")
    })
    @PostMapping("/{userId}/points")
    public ResponseEntity<WalletDto> addPoints(
            @Parameter(description = "ID of the user whose wallet points will be updated", example = "1")
            @PathVariable Long userId,

            @Parameter(description = "Number of points to add", example = "50")
            @RequestParam int points
    ) {
        return ResponseEntity.ok(walletService.addPoints(userId, points));
    }
}
