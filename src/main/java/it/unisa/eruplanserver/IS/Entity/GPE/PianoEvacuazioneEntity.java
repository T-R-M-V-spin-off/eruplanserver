package it.unisa.eruplanserver.IS.Entity.GPE;

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

    @Column(columnDefinition = "TEXT") // O tipo spaziale
    private String zonaPericolo;

    @Column(columnDefinition = "TEXT")
    private String zoneSicure;

    @Column(nullable = false)
    private String stato;
}