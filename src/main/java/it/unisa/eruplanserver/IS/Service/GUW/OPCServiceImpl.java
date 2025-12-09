package it.unisa.eruplanserver.IS.Service.GUW;

import it.unisa.eruplanserver.IS.Entity.GUW.OPCEntity;
import it.unisa.eruplanserver.IS.Exception.GUW.*;
import it.unisa.eruplanserver.IS.Repository.GUW.OPCRepository;
import it.unisa.eruplanserver.IS.Utility.Utility;
import it.unisa.eruplanserver.IS.Utility.Validator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class OPCServiceImpl implements OPCService {

    @Autowired
    private OPCRepository opcRepository;

    @Override
    @Transactional
    public void login(String codiceFiscale, String password)
            throws NoSuchAlgorithmException, OPCNotFoundException, LoginPasswordsMismatchException {
        if (!Validator.isCodiceFiscaleValid(codiceFiscale)) {
            throw new OPCNotFoundException("ERRORE - FORMATO CODICE FISCALE NON VALIDO.");
        }

        OPCEntity operatore = opcRepository.findByCodiceFiscale(codiceFiscale);
        if (operatore == null) {
            throw new OPCNotFoundException("ERRORE - OPERATORE NON TROVATO.");
        }
        String passwordHash = Utility.encrypt(password);

        if (!passwordHash.equals(operatore.getPassword())) {
            throw new LoginPasswordsMismatchException("ERRORE - PASSWORD ERRATA.");
        }
    }
}