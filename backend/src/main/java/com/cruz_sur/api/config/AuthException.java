package com.cruz_sur.api.config;


public class AuthException {

    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class UsernameAlreadyExistsException extends RuntimeException {
        public UsernameAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class VerificationCodeExpiredException extends RuntimeException {
        public VerificationCodeExpiredException(String message) {
            super(message);
        }
    }

    public static class InvalidVerificationCodeException extends RuntimeException {
        public InvalidVerificationCodeException(String message) {
            super(message);
        }
    }

    public static class AccountNotVerifiedException extends RuntimeException {
        public AccountNotVerifiedException(String message) {
            super(message);
        }
    }
}
