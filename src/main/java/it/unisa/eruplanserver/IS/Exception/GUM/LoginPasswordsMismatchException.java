package it.unisa.eruplanserver.IS.Exception.GUM;

public class LoginPasswordsMismatchException extends RuntimeException {
    public LoginPasswordsMismatchException(String message) {
        super(message);
    }
}
