package it.unisa.eruplanserver.IS.Service.GUW;

import it.unisa.eruplanserver.IS.Exception.GUW.LoginPasswordsMismatchException;
import it.unisa.eruplanserver.IS.Exception.GUW.OPCNotFoundException;
import java.security.NoSuchAlgorithmException;

public interface OPCService {

    /**
     * Esegue il login di un operatore della protezione civile.
     * * @param codiceFiscale Il CF dell'operatore.
     * @param password La password in chiaro.
     */
    void login(String codiceFiscale, String password)
            throws NoSuchAlgorithmException, OPCNotFoundException, LoginPasswordsMismatchException;
}