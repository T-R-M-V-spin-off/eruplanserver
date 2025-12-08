package it.unisa.eruplanserver.IS.Service.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.*;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Repository.GNF.*;
import it.unisa.eruplanserver.IS.Repository.GUM.URRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.unisa.eruplanserver.IS.Repository.GNF.AppoggioRepository;
import it.unisa.eruplanserver.IS.Utility.Validator;

import java.util.List;

@Service
public class GNFServiceImpl implements GNFService {
    @Autowired
    private NucleoFamiliareRepository nucleoRepository;
    @Autowired
    private RichiestaAccessoRepository richiestaRepository;
    @Autowired
    private URRepository urRepository;
    @Autowired
    private MembroRepository membroRepository;
    @Autowired
    private AppoggioRepository appoggioRepository;
    @Autowired
    private ResidenzaRepository residenzaRepository; // nuovo repository per Residenza

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

    // RF-GNF.09: Aggiunta Appoggio
    @Override
    public void aggiungiAppoggio(String cfAdmin, AppoggioEntity appoggio) throws Exception {
        UREntity admin = urRepository.findByCodiceFiscale(cfAdmin);
        if (admin == null || admin.getNucleoFamiliare() == null) {
            throw new Exception("Utente non autorizzato o nucleo non esistente.");
        }
        if (!Validator.isIndirizzoValid(
                appoggio.getViaPiazza(), appoggio.getCivico(), appoggio.getComune(),
                appoggio.getCap(), appoggio.getProvincia(), appoggio.getRegione(), appoggio.getPaese())) {
            throw new Exception("Dati indirizzo non validi.");
        }

        appoggio.setNucleoFamiliare(admin.getNucleoFamiliare());
        appoggioRepository.save(appoggio);
    }

    // RF-GNF.10: Rimozione Appoggio
    @Override
    public void rimuoviAppoggio(String cfAdmin, Long idAppoggio) throws Exception {
        UREntity admin = urRepository.findByCodiceFiscale(cfAdmin);
        if (admin == null || admin.getNucleoFamiliare() == null) {
            throw new Exception("Utente non autorizzato.");
        }

        AppoggioEntity appoggio = appoggioRepository.findById(idAppoggio)
                .orElseThrow(() -> new Exception("Appoggio non trovato."));

        // Controllo di sicurezza: l'appoggio deve appartenere al nucleo dell'admin richiedente
        if (!appoggio.getNucleoFamiliare().getId().equals(admin.getNucleoFamiliare().getId())) {
            throw new Exception("Non puoi eliminare un appoggio che non ti appartiene.");
        }

        appoggioRepository.delete(appoggio);
    }

    @Override
    public List<AppoggioEntity> getAppoggi(String cfAdmin) throws Exception {
        UREntity admin = urRepository.findByCodiceFiscale(cfAdmin);
        if (admin == null || admin.getNucleoFamiliare() == null) {
            throw new Exception("Utente non autorizzato o nucleo inesistente.");
        }
        return admin.getNucleoFamiliare().getAppoggi();
    }


    @Override
    public NucleoFamiliareEntity creaNucleoFamiliare(String cfUtente, ResidenzaEntity residenza, boolean hasVeicolo, Integer numeroPostiVeicolo) throws Exception {
        UREntity utente = urRepository.findByCodiceFiscale(cfUtente);
        if (utente == null) {
            throw new Exception("Utente non trovato con questo codice fiscale.");
        }
        if (utente.getNucleoFamiliare() != null) {
            throw new Exception("l'utente fa già parte di un nucleo familiare.");
        }
        if (residenza == null) {
            throw new Exception("é obbligatorio inserire i dati della residenza.");
        }
        if (residenza.getViaPiazza() == null || residenza.getViaPiazza().trim().isEmpty()) {
            throw new Exception("Il campo via/piazza è obbligatorio.");
        }
        if (residenza.getCivico() == null || residenza.getCivico().trim().isEmpty()) {
            throw new Exception("il campo civico è obbligatorio.");
        }
        if (residenza.getComune() == null || residenza.getComune().trim().isEmpty()) {
            throw new Exception("il campo comune è obbligatorio.");
        }
        if (residenza.getCap() == null || residenza.getCap().trim().isEmpty()) {
            throw new Exception("il campo CAP è obbligatorio.");
        }
        if (hasVeicolo) {
            if (numeroPostiVeicolo == null) {
                throw new Exception("Specifica il numero di posti che hai nel tuo veicolo.");
            }
            if (numeroPostiVeicolo > 8) {
                throw new Exception("Il numero di posti deve essere tra 1 e 8.");
            }
        } else {
            numeroPostiVeicolo = null;
        }
        //creazione del nucleo familiare
        NucleoFamiliareEntity nucleo = NucleoFamiliareEntity.builder()
                .admin(utente)
                .residenza(residenza)
                .hasVeicolo(hasVeicolo)
                .numeroPostiVeicolo(numeroPostiVeicolo)
                .build();
        NucleoFamiliareEntity nucleoSalvato = nucleoRepository.save(nucleo);

        utente.setNucleoFamiliare(nucleoSalvato);
        urRepository.save(utente);
        return nucleoSalvato;
    }

    @Override
    @Transactional
    public void aggiornaVeicolo(String cfAdmin,boolean hasVeicolo, Integer numeroPostiVeicolo) throws Exception {
        UREntity admin = urRepository.findByCodiceFiscale(cfAdmin);
        if(admin == null){
            throw new Exception("utente non trovato.");
        }
        NucleoFamiliareEntity nucleo=admin.getNucleoFamiliare();
        if(nucleo==null){
            throw new Exception("non hai un nucleo familiare.");
        }
        if(!nucleo.getAdmin().getId().equals(admin.getId())){
            throw new Exception("Solo l'amministratore può modificare i dati del veicolo");
        }
        if(hasVeicolo){
            if(numeroPostiVeicolo==null){
                throw new Exception("Se hai un veicolo, devi specificare quanti posti hai.");
            }
            if(numeroPostiVeicolo>8){
                throw new Exception("Il numero di posti deve essere tra 1 e 8.");
            }
        }else{
            numeroPostiVeicolo = null;
        }
        nucleo.setHasVeicolo(hasVeicolo);
        nucleo.setNumeroPostiVeicolo(numeroPostiVeicolo);
        nucleoRepository.save(nucleo);
    }


    // Implementazione requisito Visualizza Nucleo
    // Recupera l'utente dal CF, risale al nucleo, e restituisce la lista dei parenti.
    public List<MembroEntity> visualizzaNucleo(String cfRichiedente) {

        // 1. Cerco l'utente che ha fatto la richiesta
        MembroEntity richiedente = membroRepository.findByCodiceFiscale(cfRichiedente)
                .orElseThrow(() -> new RuntimeException("Utente non trovato nel sistema."));

        // 2. Recupero il nucleo familiare associato
        NucleoFamiliareEntity nucleo = richiedente.getNucleoFamiliare();

        if (nucleo == null) {
            throw new RuntimeException("L'utente non appartiene ad alcun nucleo familiare.");
        }

        // 3. Restituisco tutti i membri di quel nucleo
        return membroRepository.findByNucleoFamiliare(nucleo);
    }

    /**
     * RF-GNF.23: Modifica la residenza associata al nucleo.
     */
    @Override
    public void modificaResidenza(String cfAdmin, ResidenzaEntity residenza) throws Exception {
        UREntity admin = urRepository.findByCodiceFiscale(cfAdmin);
        if (admin == null) throw new Exception("Utente non trovato.");
        NucleoFamiliareEntity nucleo = admin.getNucleoFamiliare();
        if (nucleo == null) throw new Exception("Utente non associato a nessun nucleo.");

        // Verifica che l'utente sia effettivamente l'admin del nucleo
        if (!nucleo.getAdmin().getId().equals(admin.getId())) {
            throw new Exception("Permessi insufficienti: devi essere amministratore del nucleo.");
        }

        // Validazione dati indirizzo
        if (!Validator.isIndirizzoValid(
                residenza.getViaPiazza(), residenza.getCivico(), residenza.getComune(),
                residenza.getCap(), null, residenza.getRegione(), residenza.getPaese())) {
            throw new Exception("Dati residenza non validi.");
        }

        // Se il nucleo ha già una residenza, aggiorna i campi; altrimenti crea una nuova
        ResidenzaEntity current = nucleo.getResidenza();
        if (current == null) {
            // Salva la nuova residenza e collega al nucleo
            ResidenzaEntity saved = residenzaRepository.save(residenza);
            nucleo.setResidenza(saved);
            nucleoRepository.save(nucleo);
        } else {
            // Aggiorna i campi della residenza esistente
            current.setViaPiazza(residenza.getViaPiazza());
            current.setCivico(residenza.getCivico());
            current.setComune(residenza.getComune());
            current.setCap(residenza.getCap());
            current.setRegione(residenza.getRegione());
            current.setPaese(residenza.getPaese());
            residenzaRepository.save(current);
        }
    }
}
