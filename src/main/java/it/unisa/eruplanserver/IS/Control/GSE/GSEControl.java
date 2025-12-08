package it.unisa.eruplanserver.IS.Control.GSE;

import it.unisa.eruplanserver.IS.Entity.GSE.EruzioneVulcanicaEntity;
import it.unisa.eruplanserver.IS.Entity.GSE.GestioneStoricoEvacuazioniEntity;
import it.unisa.eruplanserver.IS.Exception.GSE.GSEException;
import it.unisa.eruplanserver.IS.Service.GSE.GSEService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/gestoreStorico")
public class GSEControl {

    @Autowired
    private GSEService gseService;

    private static final Logger logger = LoggerFactory.getLogger(GSEControl.class);

    /**
     * RF-GSE.21: Visualizza lista partecipanti (con stato salvo/non salvo).
     * Endpoint: GET /gestoreStorico/piani/{idPiano}
     */
    @GetMapping("/piani/{idPiano}")
    public ResponseEntity<?> visualizzaPiani(
            @PathVariable Long idPiano,
            HttpServletRequest request) {

        logger.info("GET /gestoreStorico/piani/{} called - sessionId: {}", idPiano, (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");

        // ---- Controllo autenticazione OPC ----
        Boolean isOperatore = (Boolean) request.getSession().getAttribute("isOperatore");

        if (isOperatore == null || !isOperatore) {
            // restituisce 401, Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("ACCESSO NEGATO: Devi essere un operatore (OPC) loggato.");
        }

        // ---- Recupero dati ----
        try {
            List<GestioneStoricoEvacuazioniEntity> listaPartecipanti = gseService.getPartecipantiPiano(idPiano);

           // restituisce 204
            if (listaPartecipanti == null || listaPartecipanti.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            // restituisce 200, OK con la lista nel corpo
            return ResponseEntity.ok(listaPartecipanti);

        } catch (GSEException e) {
            // restituisce 500, Internal Server Error con il messaggio dell'eccezione
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei dati dello storico: " + e.getMessage());
        }
        /*Visualizzare la lista delle eruzioni vulcaniche
        errore n. 204: no content
        errore n.503: connessione al db fallita
         */
    }
    @GetMapping("/eruzioni") public ResponseEntity<?> getListaEruzioni(HttpServletRequest request) {
        logger.info("GET /gestoreStorico/eruzioni called - sessionId: {}", (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");
        Boolean isOperatore = (Boolean) request.getSession().getAttribute("isOperatore");
        if (isOperatore == null || !isOperatore) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ACCESSO NEGATO. Devi essere un operatore (OPC) loggato.");
        }
        try{
            List<EruzioneVulcanicaEntity> eruzioni= gseService.getListaEruzioni();
            return ResponseEntity.ok(eruzioni);
        }catch(GSEException e){
            if(e.getMessage().equals("Non è stato creato nessun piano di evacuazione.")){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }
    @GetMapping("/eruzioni/ordine/crescente") public ResponseEntity<?> getEruzioniOrdineCrescente(HttpServletRequest request) {
        logger.info("GET /gestoreStorico/eruzioni/ordine/crescente called - sessionId: {}", (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");
        Boolean isOperatore = (Boolean) request.getSession().getAttribute("isOperatore");
        if (isOperatore == null || !isOperatore) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ACCESSO NEGATO. Devi essere un operatore (OPC) loggato.");
        }
        try{
            List<EruzioneVulcanicaEntity> eruzioni= gseService.getListaEruzioniCrescente();
            return ResponseEntity.ok(eruzioni);
        }catch (GSEException e){
            if (e.getMessage().equals("Non è stato creato nessun piano di evacuazione.")){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }
    @GetMapping("/eruzioni/ordine/decrescente") public ResponseEntity<?> getEruzioniOrdineDecrescente(HttpServletRequest request) {
        logger.info("GET /gestoreStorico/eruzioni/ordine/decrescente called - sessionId: {}", (request.getSession(false) != null) ? request.getSession(false).getId() : "no-session");
        Boolean isOperatore = (Boolean) request.getSession().getAttribute("isOperatore");
        if (isOperatore == null || !isOperatore) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ACCESSO NEGATO. Devi essere un operatore (OPC) loggato.");
        }
        try{
            List<EruzioneVulcanicaEntity> eruzioni= gseService.getListaEruzioniDecrescente();
            return ResponseEntity.ok(eruzioni);
        }catch (GSEException e){
            if (e.getMessage().equals("Non è stato creato nessun piano di evacuazione.")){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }
}

