package it.unisa.eruplanserver.IS.Entity.GSE;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
//entità per gestire la visualizzazione delle eruzioni vulcaniche.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table (name="eruzioni_vulcaniche")
public class EruzioneVulcanicaEntity {
    @Id
    @GeneratedValue (strategy=GenerationType.IDENTITY) private Long id;

    @Column (name="nome_evento", nullable=false) private String nomeEvento;

    @Column (name="data_inizio", nullable=false) private LocalDate dataInizio;

    @Column (name="data_fine") private LocalDate dataFine;

    @Column (name="vulcano_nome", nullable=false) private String vulcanoNome;

    @Column (name="descrizione", length = 1000) private String descrizione;



}
