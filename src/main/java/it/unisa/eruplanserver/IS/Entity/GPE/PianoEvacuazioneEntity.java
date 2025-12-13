package it.unisa.eruplanserver.IS.Entity.GPE;

import it.unisa.eruplanserver.IS.Utility.ZonaPericoloConverter;
import it.unisa.eruplanserver.IS.Utility.ZonaSicuraConverter;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "piano_evacuazione")
public class PianoEvacuazioneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 35)
    private String nome;

    @Column(nullable = false)
    private LocalDateTime dataCreazione;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = ZonaPericoloConverter.class) // Usa quello specifico per ZonaPericolo
    private ZonaPericolo zonaPericolo;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = ZonaSicuraConverter.class)   // Usa questo NUOVO converter
    private ZonaSicura zoneSicure;

    @Column(nullable = false)
    private String stato;
}