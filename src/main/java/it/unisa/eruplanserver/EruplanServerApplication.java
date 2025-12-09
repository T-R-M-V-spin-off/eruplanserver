package it.unisa.eruplanserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@RequestMapping("/utenti") // prefisso comune per tutti gli endpoint utenti
public class EruplanServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EruplanServerApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "Hello from Azure Web App - Java 21!";
    }

}