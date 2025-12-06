package it.unisa.eruplanserver.IS.Service.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.*;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Repository.GNF.*;
import it.unisa.eruplanserver.IS.Repository.GUM.URRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GNFServiceImpl implements GNFService {

    @Autowired private NucleoFamiliareRepository nucleoRepository;
    @Autowired private RichiestaAccessoRepository richiestaRepository;
    @Autowired private URRepository urRepository;
    @Autowired private MembroRepository membroRepository;

    // RF-GNF.01: Invita una persona
    public void invitaUtente(String cfAdmin, String cfInvitato) throws Exception {
        UREntity admin = urRepository.findByCodiceFiscale(cfAdmin);
        UREntity invitato = urRepository.findByCodiceFiscale(cfInvitato);

        if (admin == null) throw new Exception("Admin non trovato.");
        if (invitato == null) throw new Exception("Utente da invitare non trovato.");

        // L'invitato fa già parte di un nucleo?
        if (invitato.getNucleoFamiliare() != null) {
            throw new Exception("L'utente invitato fa già parte di un altro nucleo.");
        }

        // Crea la richiesta
        RichiestaAccessoEntity richiesta = new RichiestaAccessoEntity();
        // Collega la richiesta al nucleo
        richiesta.setNucleo(admin.getNucleoFamiliare());
        // Collega la richiesta all'utente invitato
        richiesta.setUtenteInvitato(invitato);
        // Imposta lo stato iniziale della richiesta
        richiesta.setStato("PENDING");
        // Salvataggio effettivo nel DB
        richiestaRepository.save(richiesta);
    }

    // RF-GNF.02: Accetta richiesta
    @Transactional
    public void accettaInvito(Long idRichiesta) throws Exception {
        RichiestaAccessoEntity richiesta = richiestaRepository.findById(idRichiesta)
                .orElseThrow(() -> new Exception("Richiesta non trovata"));

        if (!"PENDING".equals(richiesta.getStato())) throw new Exception("Richiesta non valida");

        UREntity utente = richiesta.getUtenteInvitato(); // Recupera l'utente invitato
        utente.setNucleoFamiliare(richiesta.getNucleo()); // Entra nel nucleo
        urRepository.save(utente);

        richiesta.setStato("ACCEPTED");
        richiestaRepository.save(richiesta);
    }

    // RF-GNF.03: Aggiungi membro manuale
    public void aggiungiMembroManuale(String cfAdmin, MembroEntity membro) throws Exception {
        UREntity admin = urRepository.findByCodiceFiscale(cfAdmin);

        if (admin.getNucleoFamiliare() == null) throw new Exception("Non hai ancora un nucleo.");

        membro.setNucleoFamiliare(admin.getNucleoFamiliare());
        membroRepository.save(membro);
    }

    // RF-GNF.04: Abbandona nucleo
    public void abbandonaNucleo(String cfUtente) throws Exception {
        UREntity utente = urRepository.findByCodiceFiscale(cfUtente);

        if (utente.getNucleoFamiliare() == null) throw new Exception("Non sei in nessun nucleo.");

        utente.setNucleoFamiliare(null); // Rimuove l'associazione con il nucleo
        urRepository.save(utente);

        // TODO: Se era l'admin, bisognerebbe gestire il passaggio di admin ad un altro membro
    }

    // RF-GNF.06: Visualizza lista richieste
    public List<RichiestaAccessoEntity> getRichiestePendenti(String cfUtente) {
        UREntity utente = urRepository.findByCodiceFiscale(cfUtente);
        return richiestaRepository.findByUtenteInvitatoIdAndStato(utente.getId(), "PENDING");
    }
}