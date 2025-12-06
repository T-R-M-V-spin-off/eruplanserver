package it.unisa.eruplanserver.IS.Entity.GNF;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "membro")
public class MembroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(length = 16)
    private String codiceFiscale;

    private LocalDate dataNascita;
    private String sesso;

    // Checkbox dello scenario
    private boolean richiedeAssistenza;
    private boolean isMinorenne;

    @ManyToOne
    @JoinColumn(name = "nucleo_id", nullable = false)
    @JsonIgnore
    private NucleoFamiliareEntity nucleoFamiliare;
}
