package it.unisa.eruplanserver.IS.Service.GUM;

import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Exception.GUM.*;
import it.unisa.eruplanserver.IS.Repository.GUM.URRepository;
import it.unisa.eruplanserver.IS.Utility.Utility;
import it.unisa.eruplanserver.IS.Utility.Validator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class URServiceImpl implements URService {

    @Autowired
    private URRepository urRepository;

    @Override
    @Transactional
    public void login(String codiceFiscale, String password)
            throws NoSuchAlgorithmException, URNotFoundException, LoginPasswordsMismatchException {

        // Validazione Formale
        if (!Validator.isCodiceFiscaleValid(codiceFiscale)) {
            throw new URNotFoundException("ERRORE - FORMATO CODICE FISCALE NON VALIDO.");
        }

        // Cerca nel DB
        UREntity cittadino = urRepository.findByCodiceFiscale(codiceFiscale);

        // Controllo null manuale come in OPCServiceImpl
        if (cittadino == null) {
            throw new URNotFoundException("ERRORE - UTENTE NON TROVATO.");
        }

        // Verifica Password
        String passwordHash = Utility.encrypt(password);

        if (!passwordHash.equals(cittadino.getPassword())) {
            throw new LoginPasswordsMismatchException("ERRORE - PASSWORD ERRATA.");
        }
    }
}