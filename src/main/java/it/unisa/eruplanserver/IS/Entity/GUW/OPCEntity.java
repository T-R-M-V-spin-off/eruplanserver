package it.unisa.eruplanserver.IS.Entity.GUW;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "operatore")
public class OPCEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String codiceFiscale;

    @Column(nullable = false)
    private String password;

    @Column(length = 30)
    private String nome;

    @Column(length = 30)
    private String cognome;
}