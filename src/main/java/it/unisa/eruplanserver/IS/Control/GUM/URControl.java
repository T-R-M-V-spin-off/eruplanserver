package it.unisa.eruplanserver.IS.Control.GUM;

import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Exception.GUM.GUMException;
import it.unisa.eruplanserver.IS.Exception.GUM.InvalidURDataException;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

@Controller
@RequestMapping("/gestoreUtentiMobile") // URL ipotizzato per il modulo GUM
public class URControl {

    @Autowired
    private URService urService;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    @RequestMapping(value = "/registra", method = RequestMethod.POST)
    public void registra(@RequestBody String body, HttpServletRequest request, HttpServletResponse response)
            throws IOException, GUMException, InvalidURDataException {

        JSONParser parser = new JSONParser();

        try {
            JSONObject json = (JSONObject) parser.parse(body);

            String nome = (String) json.get("nome");
            String cognome = (String) json.get("cognome");
            String cf = (String) json.get("codiceFiscale");
            String password = (String) json.get("password");
            String sesso = (String) json.get("sesso");

            String dataStr = (String) json.get("dataNascita");
            LocalDate dataNascita = null;

            if (dataStr != null && !dataStr.isEmpty()) {
                dataNascita = LocalDate.parse(dataStr, FORMATO_DATA);
            }

            // Validazione
            if (nome == null || cognome == null || cf == null || password == null || sesso == null || dataNascita == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Errore: tutti i campi sono obbligatori!");
                return;
            }

            // Creazione utente
            UREntity nuovoUtente = new UREntity();
            nuovoUtente.setNome(nome.trim());
            nuovoUtente.setCognome(cognome.trim());
            nuovoUtente.setCodiceFiscale(cf.trim().toUpperCase());
            nuovoUtente.setPassword(password);
            nuovoUtente.setSesso(sesso.trim().toUpperCase());
            nuovoUtente.setDataDiNascita(dataNascita);

            urService.registra(nuovoUtente);

            response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
            response.setContentType("application/json");

            JSONObject successResponse = new JSONObject();
            successResponse.put("success", true);
            successResponse.put("message", "Registrazione completata con successo");
            successResponse.put("codiceFiscale", cf);
            response.getWriter().write(successResponse.toJSONString());

        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Errore: formato JSON non valido");
        } catch (DateTimeParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato data errato. Usa: GG-MM-AAAA");
        } catch (InvalidURDataException | URNotFoundException e) {
            //Catturiamo l'errore di validazione e restituiamo 400 invece di 500
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore tecnico del server");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore interno: " + e.getMessage());
        }
    }
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