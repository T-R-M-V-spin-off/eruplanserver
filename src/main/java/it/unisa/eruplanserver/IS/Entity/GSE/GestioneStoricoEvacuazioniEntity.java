package it.unisa.eruplanserver.IS.Entity.GSE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.unisa.eruplanserver.IS.Entity.GPE.PianoEvacuazioneEntity;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "storico_evacuazioni")
public class GestioneStoricoEvacuazioniEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "piano_id", nullable = false)
    @JsonIgnore //consigliato da gemini per evitare loop infiniti quando si stampa la lista
    private PianoEvacuazioneEntity piano;

    @ManyToOne
    @JoinColumn(name = "cittadino_id", nullable = false)
    private UREntity cittadino;

    @Column(nullable = false)
    private boolean isSalvo; // true = salvato

    //PLACEHOLDER - non ho capito se serve l'id nella classe, essendo una lista di piani di evacuazione

}