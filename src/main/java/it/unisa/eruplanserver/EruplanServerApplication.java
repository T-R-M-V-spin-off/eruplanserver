package it.unisa.eruplanserver;

import it.unisa.eruplanserver.Entity.UtenteEntity;
import it.unisa.eruplanserver.Repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/utenti") // prefisso comune per tutti gli endpoint utenti
public class EruplanServerApplication {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(EruplanServerApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "Hello from Azure Web App - Java 21!";
    }

    // --------- Endpoint utenti ---------
    @GetMapping("/all")
    public List<UtenteEntity> getAllUtenti() {
        return utenteRepository.findAll();
    }

    @GetMapping("/email/{email}")
    public UtenteEntity getUtenteByEmail(@PathVariable String email) {
        return utenteRepository.findByEmail(email);
    }

    @GetMapping("/nome/{nome}")
    public List<UtenteEntity> getUtentiByNome(@PathVariable String nome) {
        return utenteRepository.findByNome(nome);
    }

    @GetMapping("/con-ordini")
    public List<UtenteEntity> getUtentiConOrdini() {
        return utenteRepository.findUtentiConOrdini();
    }

    @GetMapping("/con-profilo/{email}")
    public UtenteEntity getUtenteConProfilo(@PathVariable String email) {
        return utenteRepository.findUtenteConProfilo(email);
    }

    @PostMapping("/add")
    public UtenteEntity addUtente(@RequestBody UtenteEntity utente) {
        return utenteRepository.save(utente);
    }
}