package it.unisa.eruplanserver.IS.Control.GPE;

import it.unisa.eruplanserver.IS.Exception.GPE.GPEException;
import it.unisa.eruplanserver.IS.Service.GPE.GPEService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/gestorePiani")
public class GPEControl {

    @Autowired
    private GPEService gpeService;

    /**
     * Gestisce la richiesta di generazione di un piano di evacuazione.
     */
    @PostMapping("/genera")
    public void generaPiano(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONParser parser = new JSONParser();
        try {
            // Verifica sessione operatore (GUW)

            if (request.getSession().getAttribute("isOperatore") == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Accesso negato: Operatore non loggato.");
                return;
            }

            JSONObject json = (JSONObject) parser.parse(body);
            String nome = (String) json.get("nome");

            Object zonaPericoloObj = json.get("zonaPericolo");
            String zonaPericolo = (zonaPericoloObj != null) ? zonaPericoloObj.toString() : null;

            Object zoneSicureObj = json.get("zoneSicure");
            String zoneSicure = (zoneSicureObj != null) ? zoneSicureObj.toString() : null;
            gpeService.generaPiano(nome, zonaPericolo, zoneSicure);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\": \"Piano generato e notifiche inviate con successo.\"}");

        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Errore nel parsing del JSON.");
        } catch (GPEException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore generico del server.");
        }
    }
}