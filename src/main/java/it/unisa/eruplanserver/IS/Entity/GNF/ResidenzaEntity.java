package it.unisa.eruplanserver.IS.Entity.GNF;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "residenza")
public class ResidenzaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String viaPiazza;

    @Column(nullable = false)
    private String provincia;

    @Column(nullable = false)
    private String civico;

    @Column(nullable = false)
    private String comune;

    @Column(nullable = false)
    private String cap;

    @Column(nullable = false)
    private String regione;

    @Column(nullable = false)
    private String paese;
}