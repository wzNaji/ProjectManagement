package com.boefcity.projectmanagement.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Bruges til at generere krypterede passwords til test-brug
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String rawPassword1 = "password1";
        String rawPassword2 = "password2";
        String rawPassword3 = "password3";

        String encodedPassword1 = passwordEncoder.encode(rawPassword1);
        String encodedPassword2 = passwordEncoder.encode(rawPassword2);
        String encodedPassword3 = passwordEncoder.encode(rawPassword3);

        System.out.println("Encoded password1: " + encodedPassword1);
        System.out.println("Encoded password2: " + encodedPassword2);
        System.out.println("Encoded password3: " + encodedPassword3);
    }
}
