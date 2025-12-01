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
    @Override
    @Transactional
    public void registra(UREntity nuovoUtente) throws InvalidURDataException,NoSuchAlgorithmException {
        if (!Validator.isCodiceFiscaleValid(nuovoUtente.getCodiceFiscale())) {
            throw new URNotFoundException("ERRORE - FORMATO CODICE FISCALE NON VALIDO.");
        }
        if (!Validator.isPasswordValid(nuovoUtente.getPassword())){
            throw  new InvalidURDataException("errore: password non valida");
        }
        if(!Validator.isNomeValid(nuovoUtente.getNome())){
            throw new InvalidURDataException("errore: inserisci un nome conforme");
        }
        if(!Validator.isSessoValid(nuovoUtente.getSesso())){
            throw new InvalidURDataException("errore: sesso non identificato");
        }
        if (!Validator.isDataNascitaValid(nuovoUtente.getDataDiNascita())) {
            throw new InvalidURDataException("errore: inserisci una data valida");
        }
        if (!Validator.isCognomeValid(nuovoUtente.getCognome())){
            throw new InvalidURDataException("errore: inserisci un cognome conforme!");
        }
        if(urRepository.existsByCodiceFiscale(nuovoUtente.getCodiceFiscale())){
            throw new InvalidURDataException(("errore: codice fiscale già registrato"));
        }
        String passwordOriginale= nuovoUtente.getPassword();
        String passwordCrittografata=Utility.encrypt(passwordOriginale);
        nuovoUtente.setPassword(passwordCrittografata);
        urRepository.save(nuovoUtente);
    }
    }

