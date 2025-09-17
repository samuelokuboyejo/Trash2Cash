package com.trash2cash.wallet;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trash2cash.users.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "wallet", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id")
})
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal balance = BigDecimal.ZERO;
    private Long points = 0L;

    @OneToOne
    @JoinColumn(name = "user_id",   nullable = false, unique = true, updatable = false)
    @JsonIgnore
    private User user;

    private LocalDateTime updatedAt = LocalDateTime.now();
}
