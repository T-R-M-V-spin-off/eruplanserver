package it.unisa.eruplanserver.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="profilo")
public class ProfiloEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String indirizzo;
    private String telefono;

    @OneToOne
    @JoinColumn(name = "utente_email")
    private UtenteEntity utente;
}