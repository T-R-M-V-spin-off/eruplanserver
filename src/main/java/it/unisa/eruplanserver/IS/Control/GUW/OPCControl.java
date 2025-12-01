package it.unisa.eruplanserver.IS.Control.GUW;

import it.unisa.eruplanserver.IS.Exception.GUW.GUWException;
import it.unisa.eruplanserver.IS.Exception.GUW.LoginPasswordsMismatchException;
import it.unisa.eruplanserver.IS.Exception.GUW.OPCNotFoundException;
import it.unisa.eruplanserver.IS.Service.GUW.OPCService;
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
@RequestMapping("/gestoreUtentiWeb")
public class OPCControl {

    @Autowired
    private OPCService opcService;

    /**
     * Gestisce il login dell'operatore.
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(@RequestBody String body, HttpServletRequest request, HttpServletResponse response)
            throws GUWException {

        JSONParser parser = new JSONParser();

        try {
            JSONObject json = (JSONObject) parser.parse(body);
            String cf = (String) json.get("codiceFiscale");
            String password = (String) json.get("password");

            // Chiama il service
            opcService.login(cf, password);

            request.getSession().setAttribute("isOperatore", true);
            request.getSession().setAttribute("codiceFiscale", cf);

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (ParseException | NoSuchAlgorithmException e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERRORE TECNICO");
            } catch (IOException ex) {
                throw new GUWException("ERRORE INTERNAL SERVER.");
            }
        } catch (OPCNotFoundException e) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            } catch (IOException ex) {
                throw new GUWException("ERRORE - OPERATORE NON TROVATO.");
            }
        } catch (LoginPasswordsMismatchException e) {
            try {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            } catch (IOException ex) {
                throw new GUWException("ERRORE - PASSWORD ERRATA.");
            }
        }
    }
}