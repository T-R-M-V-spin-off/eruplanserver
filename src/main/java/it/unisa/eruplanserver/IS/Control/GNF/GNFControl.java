package it.unisa.eruplanserver.IS.Control.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.MembroEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.NucleoFamiliareEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.ResidenzaEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.CreazioneNucleoRequest;
import it.unisa.eruplanserver.IS.Entity.GNF.RichiestaAccessoEntity;
import it.unisa.eruplanserver.IS.Service.GNF.GNFServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.unisa.eruplanserver.IS.Entity.GNF.AppoggioEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.ResidenzaEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.AggiornaVeicoloRequest;

import java.util.List;

@RestController
@RequestMapping("/gestoreNucleo") // URL Base confermato dal team
public class GNFControl {

    @Autowired
    private GNFServiceImpl gnfService;

    private static final Logger logger = LoggerFactory.getLogger(GNFControl.class);

    // RF-GNF.07: Visualizza Nucleo
    @GetMapping("/membri")
    public ResponseEntity<?> getMembri(HttpServletRequest request) {
        logger.info("GET /gestoreNucleo/membri called - sessionId: {}", (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");
        // 1. Recupero il CF dalla sessione
        String cfUtente = (String) request.getSession().getAttribute("codiceFiscale");

        if (cfUtente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login richiesto");
        }

        try {
            // 2. Chiamata al service
            // Restituisce la lista di MembroEntity (con campi e date formattati come stringhe per il mobile)
            List<MembroEntity> membri = gnfService.visualizzaNucleo(cfUtente);

            // 3. Ritorna HTTP 200 con il JSON Array
            return ResponseEntity.ok(membri);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // RF-GNF.01: Invita utente
    @PostMapping("/invita")
    public ResponseEntity<String> invitaUtente(@RequestParam String cfInvitato, HttpServletRequest request) {
        logger.info("POST /gestoreNucleo/invita called - cfInvitato: {} - sessionId: {}", cfInvitato, (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");
        String cfAdmin = (String) request.getSession().getAttribute("codiceFiscale");
        if (cfAdmin == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login richiesto");

        // VALIDAZIONE: lunghezza e caratteri del codice fiscale invitato
        if (cfInvitato == null || cfInvitato.length() != 16) {
            return ResponseEntity.badRequest().body("L'invito di un utente nel proprio nucleo familiare non viene effettuato dato che il campo \"CodiceFiscale\" non è composto da 16 caratteri.");
        }
        if (!cfInvitato.matches("^[A-Za-z1-9]{16}$")) {
            return ResponseEntity.badRequest().body("L'invito di un utente nel proprio nucleo familiare non viene effettuato dato che il campo \"CodiceFiscale\" contiene caratteri non validi.");
        }

        try {
            gnfService.invitaUtente(cfAdmin, cfInvitato);
            return ResponseEntity.ok("Invito inviato con successo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // RF-GNF.02: Accetta invito
    @PostMapping("/accetta/{idRichiesta}")
    public ResponseEntity<String> accettaInvito(@PathVariable Long idRichiesta) {
        logger.info("POST /gestoreNucleo/accetta called - idRichiesta: {}", idRichiesta);
        try {
            gnfService.accettaInvito(idRichiesta);
            return ResponseEntity.ok("Benvenuto nel nucleo!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // RF-GNF.03: Aggiungi Membro Manuale
    @PostMapping("/aggiungiMembro")
    public ResponseEntity<String> aggiungiMembro(@RequestBody MembroEntity membro, HttpServletRequest request) {
        logger.info("POST /gestoreNucleo/aggiungiMembro called - membro: {} - sessionId: {}", membro, (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");
        String cfAdmin = (String) request.getSession().getAttribute("codiceFiscale");
        if (cfAdmin == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login richiesto");

        try {
            gnfService.aggiungiMembroManuale(cfAdmin, membro);
            return ResponseEntity.ok("Membro aggiunto al nucleo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // RF-GNF.04: Abbandona Nucleo
    @PostMapping("/abbandona")
    public ResponseEntity<String> abbandonaNucleo(HttpServletRequest request) {
        logger.info("POST /gestoreNucleo/abbandona called - sessionId: {}", (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");
        String cfUtente = (String) request.getSession().getAttribute("codiceFiscale");
        if (cfUtente == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login richiesto");

        try {
            gnfService.abbandonaNucleo(cfUtente);
            return ResponseEntity.ok("Hai abbandonato il nucleo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // RF-GNF.06: Visualizza lista richieste
    @GetMapping("/richieste")
    public ResponseEntity<List<RichiestaAccessoEntity>> getRichieste(HttpServletRequest request) {
        logger.info("GET /gestoreNucleo/richieste called - sessionId: {}", (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");
        String cfUtente = (String) request.getSession().getAttribute("codiceFiscale");
        if (cfUtente == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(gnfService.getRichiestePendenti(cfUtente));
    }

    // RF-GNF.09: Aggiungi Appoggio
    @PostMapping("/appoggi/aggiungi")
    public ResponseEntity<String> aggiungiAppoggio(@RequestBody AppoggioEntity appoggio, HttpServletRequest request) {
        logger.info("POST /gestoreNucleo/appoggi/aggiungi called - appoggio: {} - sessionId: {}", appoggio, (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");
        String cfAdmin = (String) request.getSession().getAttribute("codiceFiscale");
        if (cfAdmin == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login richiesto");

        try {
            gnfService.aggiungiAppoggio(cfAdmin, appoggio);
            return ResponseEntity.ok("Appoggio aggiunto con successo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Mostra schermata con luoghi sicuri
    @GetMapping("/appoggi")
    public ResponseEntity<List<AppoggioEntity>> getAppoggi(HttpServletRequest request) {
        logger.info("GET /gestoreNucleo/appoggi called - sessionId: {}", (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");
        String cfAdmin = (String) request.getSession().getAttribute("codiceFiscale");
        if (cfAdmin == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            List<AppoggioEntity> appoggi = gnfService.getAppoggi(cfAdmin);
            return ResponseEntity.ok(appoggi);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // RF-GNF.10 : Rimozione di appoggio
    @DeleteMapping("/appoggi/rimuovi/{id}")
    public ResponseEntity<String> rimuoviAppoggio(@PathVariable Long id, HttpServletRequest request) {
        logger.info("DELETE /gestoreNucleo/appoggi/rimuovi/{} called - sessionId: {}", id, (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");
        String cfAdmin = (String) request.getSession().getAttribute("codiceFiscale");
        if (cfAdmin == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login richiesto");
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("ID appoggio non valido.");
        }

        try {
            gnfService.rimuoviAppoggio(cfAdmin, id);
            return ResponseEntity.ok("Appoggio rimosso con successo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //RF-GNF.12: aggiorna dati del veicolo
    @PutMapping("/veicolo")
    public ResponseEntity<String> aggiornaveicolo(@RequestBody AggiornaVeicoloRequest request, HttpServletRequest httpRequest) {
        String cfAdmin = (String) httpRequest.getSession().getAttribute("codiceFiscale");
        if(cfAdmin==null){
            return ResponseEntity.badRequest().body("Login richiesto");
        }try {
            gnfService.aggiornaVeicolo(cfAdmin, request.isHasVeicolo(), request.getNumeroPostiVeicolo());
            return ResponseEntity.ok("dati aggiornati con successo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("errore" + e.getMessage());
        }
        }


    //RF-GNF.08: Creazione del nucleo familiare
    @PostMapping("/crea")
    public ResponseEntity<?> creaNucleoFamiliare(@RequestBody CreazioneNucleoRequest request, HttpServletRequest httpRequest) {
        logger.info("POST /gestoreNucleo/crea called - request: {} - sessionId: {}", request, (httpRequest.getSession(false) != null) ? httpRequest.getSession(false).getId() : "no-session");
        String cfUtente = (String) httpRequest.getSession().getAttribute("codiceFiscale");
        if(cfUtente==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login richiesto");
        }
        try{
            NucleoFamiliareEntity nucleoCreato=gnfService.creaNucleoFamiliare(
                    cfUtente,
                    request.getResidenza(),
                    request.isHasVeicolo(),
                    request.getNumeroPostiVeicolo()
            );
            return ResponseEntity.ok(nucleoCreato);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("errore. "+ e.getMessage());

        }
    }

    // RF-GNF.23: Modifica residenza del nucleo
    @PostMapping("/residenza/modifica")
    public ResponseEntity<String> modificaResidenza(@RequestBody ResidenzaEntity residenza, HttpServletRequest request) {
        logger.info("POST /gestoreNucleo/residenza/modifica called - residenza: {} - sessionId: {}", residenza, (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");
        String cfAdmin = (String) request.getSession().getAttribute("codiceFiscale");
        if (cfAdmin == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login richiesto");

        try {
            gnfService.modificaResidenza(cfAdmin, residenza);
            return ResponseEntity.ok("Residenza aggiornata con successo.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
