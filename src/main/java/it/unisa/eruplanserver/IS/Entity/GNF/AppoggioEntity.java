package it.unisa.eruplanserver.IS.Entity.GNF;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "appoggio")
public class AppoggioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String viaPiazza;

    @Column(nullable = false, length = 6)
    private String civico;

    @Column(nullable = false, length = 40)
    private String comune;

    @Column(nullable = false, length = 5)
    private String cap;

    @Column(nullable = false, length = 20)
    private String provincia;

    @Column(nullable = false, length = 25)
    private String regione;

    @Column(nullable = false, length = 40)
    private String paese;

    @ManyToOne
    @JoinColumn(name = "nucleo_id", nullable = false)
    @JsonIgnore
    private NucleoFamiliareEntity nucleoFamiliare;
}