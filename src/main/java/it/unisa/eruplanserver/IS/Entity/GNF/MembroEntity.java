package it.unisa.eruplanserver.IS.Entity.GNF;

import com.fasterxml.jackson.annotation.JsonIgnore;
// import it.unisa.eruplanserver.IS.Entity.GNF.NucleoFamiliareEntity; // (Già presente nel package)
import jakarta.persistence.*;
import lombok.*;

// [MODIFICA RICHIESTA] Rimosso LocalDate per usare String come vuole il mobile
// import java.time.LocalDate;

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

    // Lato Mobile (riga 136): m.setDataDiNascita(obj.optString("dataDiNascita"));
    // Il mobile si aspetta una Stringa, non un oggetto Date.
    private String dataDiNascita;

    private String sesso;

    // Lato Mobile (riga 138): m.setAssistenza(obj.optBoolean("assistenza"));
    // Deve chiamarsi "assistenza", non "richiedeAssistenza".
    // Cambiato a Boolean (oggetto) per permettere null e validazione
    private Boolean assistenza;

    // Lato Mobile (riga 139): m.setMinorenne(obj.optBoolean("minorenne"));
    // Deve chiamarsi "minorenne", non "isMinorenne".
    // Cambiato a Boolean (oggetto) per permettere null e validazione
    private Boolean minorenne;

    @ManyToOne
    @JoinColumn(name = "nucleo_id", nullable = false)
    @JsonIgnore // Essenziale per evitare loop infiniti nel JSON
    private NucleoFamiliareEntity nucleoFamiliare;
}