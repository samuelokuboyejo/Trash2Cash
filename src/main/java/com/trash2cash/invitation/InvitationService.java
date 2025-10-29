package com.trash2cash.invitation;

import com.trash2cash.users.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {
    private final InvitationRepository invitationRepository;
//    private final EmailService emailService;
    private final UserRepository userRepository;

    @Value("${app.invitation.link}")
    private String link;

    public InvitationResponse createInvitation(InvitationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists!");
        }
        var invitation = Invitation.builder()
                .email(request.getEmail())
                .invitedRole(request.getRole())
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        Invitation savedInvitation = invitationRepository.save(invitation);
        String invitationLink = link + savedInvitation.getToken();

//        emailService.sendInvitationEmail(savedInvitation.getEmail(), "Registration Invitation", invitationLink);

        return InvitationResponse.builder()
                .invitationLink(invitationLink)
                .email(savedInvitation.getEmail())
                .build();
    }
}
