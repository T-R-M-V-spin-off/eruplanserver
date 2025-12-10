package it.unisa.eruplanserver.IS.Entity.GUM;

import it.unisa.eruplanserver.IS.Entity.GNF.NucleoFamiliareEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "utente_registrato")
public class UREntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nucleo_familiare_id")
    private NucleoFamiliareEntity nucleoFamiliare;

    @Column(nullable = false, length = 30)
    private String nome;

    @Column(nullable = false, length = 30)
    private String cognome;

    @Column(nullable = false, unique = true, length = 16)
    private String codiceFiscale;

    @Column(nullable = false)
    private String password;

    @Column(name = "data_nascita")
    private LocalDate dataDiNascita;

    @Column(nullable = false, length = 1)
    private String sesso;

}