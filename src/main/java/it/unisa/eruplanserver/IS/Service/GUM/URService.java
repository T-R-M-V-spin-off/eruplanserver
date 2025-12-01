package it.unisa.eruplanserver.IS.Service.GUM;

import it.unisa.eruplanserver.IS.Exception.GUM.LoginPasswordsMismatchException;
import it.unisa.eruplanserver.IS.Exception.GUM.URNotFoundException;
import java.security.NoSuchAlgorithmException;

public interface URService {

    // Esegue il login di un cittadino (Utente Registrato).

    void login(String codiceFiscale, String password)
            throws NoSuchAlgorithmException, URNotFoundException, LoginPasswordsMismatchException;
}