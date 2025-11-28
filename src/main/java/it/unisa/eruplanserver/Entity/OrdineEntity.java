package it.unisa.eruplanserver.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="ordine")
public class OrdineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prodotto;
    private int quantita;

    @ManyToOne
    @JoinColumn(name = "utente_email")
    private UtenteEntity utente;
}