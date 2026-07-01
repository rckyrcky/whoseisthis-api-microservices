package com.whoseisthis.users.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final BCryptPasswordEncoder passwordEncoder;

    public String hash(String plainText) {
        return passwordEncoder.encode(plainText);
    }

    public boolean compare(String plainText, String hashed) {
        return passwordEncoder.matches(plainText, hashed);
    }
}
