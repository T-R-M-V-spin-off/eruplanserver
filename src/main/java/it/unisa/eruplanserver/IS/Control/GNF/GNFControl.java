package it.unisa.eruplanserver.IS.Control.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.MembroEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.RichiestaAccessoEntity;
import it.unisa.eruplanserver.IS.Service.GNF.GNFServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gestoreNucleo")
public class GNFControl {

    @Autowired
    private GNFServiceImpl gnfService;

    // RF-GNF.01: Invita utente
    @PostMapping("/invita")
    public ResponseEntity<String> invitaUtente(@RequestParam String cfInvitato, HttpServletRequest request) {
        String cfAdmin = (String) request.getSession().getAttribute("codiceFiscale");
        if (cfAdmin == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login richiesto");

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
        String cfUtente = (String) request.getSession().getAttribute("codiceFiscale");
        if (cfUtente == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(gnfService.getRichiestePendenti(cfUtente));
    }
}