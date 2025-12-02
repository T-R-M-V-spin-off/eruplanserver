package it.unisa.eruplanserver.IS.Service.GPE;

import it.unisa.eruplanserver.IS.Entity.GPE.PianoEvacuazioneEntity;
import it.unisa.eruplanserver.IS.Exception.GPE.GPEException;
import it.unisa.eruplanserver.IS.Repository.GPE.GPERepository;
import it.unisa.eruplanserver.IS.Utility.Validator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class GPEServiceImpl implements GPEService {

    @Autowired
    private GPERepository gpeRepository;

    @Autowired
    private FirebaseService firebaseService;
    // Qui inietteremo il Service/Repository GNF per recuperare i nuclei

    @Override
    @Transactional
    public void generaPiano(String nome, String zonaPericolo, String zoneSicure) throws GPEException {

        if (!Validator.isNomePianoValid(nome)) {
            throw new GPEException("ERRORE - NOME PIANO NON VALIDO (Deve contenere solo lettere/numeri e avere 3-35 caratteri).");
        }
        if (gpeRepository.existsByNome(nome)) {
            throw new GPEException("ERRORE - ESISTE GIÀ UN PIANO CON QUESTO NOME.");
        }

        // Creazione e Salvataggio Piano
        PianoEvacuazioneEntity piano = new PianoEvacuazioneEntity();
        piano.setNome(nome);
        piano.setDataCreazione(LocalDateTime.now());
        piano.setZonaPericolo(zonaPericolo);
        piano.setZoneSicure(zoneSicure);
        piano.setStato("ATTIVO");

        // Qui andrebbe la logica dell'algoritmo di assegnazione percorsi (RF-GPE.19)

        gpeRepository.save(piano);

        // Notifica (RF-GPE.15)
        inviaNotifiche(piano);
    }

    private void inviaNotifiche(PianoEvacuazioneEntity piano) {
        String titolo = "⚠️ ALLERTA EVACUAZIONE ⚠️";
        String corpo = "È stato attivato il piano: " + piano.getNome() + ". Controlla il tuo percorso!";

        // Inviamo al topic "emergenza"
        firebaseService.inviaNotificaBroadcast(titolo, corpo, "emergenza");
    }
}