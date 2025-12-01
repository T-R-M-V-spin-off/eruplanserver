package it.unisa.eruplanserver.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String codiceFiscale;
    private String password;
}