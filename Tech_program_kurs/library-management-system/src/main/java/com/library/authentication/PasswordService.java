package com.library.authentication;

public class PasswordService {
    public boolean validatePassword(String plainPassword, String hashedPassword) {
        return plainPassword.equals(hashedPassword);
    }

    public String hashPassword(String password) {
        return password;
    }
}