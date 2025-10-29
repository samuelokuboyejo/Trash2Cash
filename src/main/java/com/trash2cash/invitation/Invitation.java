package com.trash2cash.invitation;

import com.trash2cash.users.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String email;
    private String token;

    @Enumerated(EnumType.STRING)
    private UserRole invitedRole;

    private LocalDateTime expiresAt;
    private boolean used = false;
}
