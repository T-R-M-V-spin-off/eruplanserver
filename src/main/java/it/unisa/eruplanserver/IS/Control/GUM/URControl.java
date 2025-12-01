package it.unisa.eruplanserver.IS.Control.GUM;

import it.unisa.eruplanserver.IS.Exception.GUM.GUMException;
import it.unisa.eruplanserver.IS.Exception.GUM.LoginPasswordsMismatchException;
import it.unisa.eruplanserver.IS.Exception.GUM.URNotFoundException;
import it.unisa.eruplanserver.IS.Service.GUM.URService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/gestoreUtentiMobile") // URL ipotizzato per il modulo GUM
public class URControl {

    @Autowired
    private URService urService;

    /**
     * RF-GUM.25: Gestisce il login del cittadino.
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(@RequestBody String body, HttpServletRequest request, HttpServletResponse response)
            throws GUMException {

        JSONParser parser = new JSONParser();

        try {
            JSONObject json = (JSONObject) parser.parse(body);
            String cf = (String) json.get("codiceFiscale");
            String password = (String) json.get("password");

            // Chiama il service
            urService.login(cf, password);

            // Imposta attributi di sessione per il cittadino
            request.getSession().setAttribute("isCittadino", true);
            request.getSession().setAttribute("codiceFiscale", cf);

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (ParseException | NoSuchAlgorithmException e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERRORE TECNICO");
            } catch (IOException ex) {
                throw new GUMException("ERRORE INTERNAL SERVER.");
            }
        } catch (URNotFoundException e) {
            try {
                // Utente non trovato o CF non valido
                response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            } catch (IOException ex) {
                throw new GUMException("ERRORE - UTENTE NON TROVATO.");
            }
        } catch (LoginPasswordsMismatchException e) {
            try {
                // Password errata
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            } catch (IOException ex) {
                throw new GUMException("ERRORE - PASSWORD ERRATA.");
            }
        }
    }

    /**
     * RF-GUM.26: Gestisce il logout del cittadino.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalida la sessione esistente rimuovendo tutti gli attributi
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}