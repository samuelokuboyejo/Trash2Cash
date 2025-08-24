package com.trash2cash.users.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fpId;

    @Column(nullable = false)
    private Integer otp;

    @Column(nullable = false)
    private Date expirationTime;

    @OneToOne
    private User user;
}
