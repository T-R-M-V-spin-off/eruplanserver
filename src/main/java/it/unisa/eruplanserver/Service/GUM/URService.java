package it.unisa.eruplanserver.Service.GUM;

import it.unisa.eruplanserver.DTO.LoginRequest;
import it.unisa.eruplanserver.Entity.GUM.UREntity;
import it.unisa.eruplanserver.Repository.GUM.URRepository;
import it.unisa.eruplanserver.Utility.Utility;
import it.unisa.eruplanserver.Utility.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class URService {

    @Autowired
    private URRepository urRepository;

    /**
     * RF-GUM.25: Log-in del cittadino
     */
    public UREntity login(LoginRequest request) throws Exception {
        // Validazione input
        if (!Validator.isCodiceFiscaleValid(request.getCodiceFiscale())) {
            throw new IllegalArgumentException("Formato Codice Fiscale non valido");
        }
        if (!Validator.isPasswordValid(request.getPassword())) {
            throw new IllegalArgumentException("Formato Password non valido");
        }

        // Cerca l'utente nel DB (restituisce UREntity o null)
        UREntity cittadino = urRepository.findByCodiceFiscale(request.getCodiceFiscale());


        if (cittadino == null) {
            throw new Exception("Utente non trovato");
        }

        // Cifra la password inserita e confrontala con quella nel DB
        try {
            String passwordCriptata = Utility.encrypt(request.getPassword());

            if (!passwordCriptata.equals(cittadino.getPassword())) {
                throw new Exception("Password errata");
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore interno durante la cifratura password");
        }

        return cittadino;
    }
}