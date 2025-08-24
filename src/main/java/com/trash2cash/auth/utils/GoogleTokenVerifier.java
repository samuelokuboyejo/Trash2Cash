package com.trash2cash.auth.utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class GoogleTokenVerifier {
    @Value("${CLIENT_ID}")
    private static String CLIENT_ID;

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(
            @Value("${google.oauth.client-ids}") List<String> clientIds
    ) throws Exception {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance()
        ).setAudience(clientIds)
                .build();
    }

    /** Returns payload if valid, else throws IllegalArgumentException */
    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new IllegalArgumentException("Invalid Google ID token");
            }
            return idToken.getPayload();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to verify Google ID token", e);
        }
    }
}
