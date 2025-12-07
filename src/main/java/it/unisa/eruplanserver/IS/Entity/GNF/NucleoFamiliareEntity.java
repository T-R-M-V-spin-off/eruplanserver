package it.unisa.eruplanserver.IS.Entity.GNF;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "nucleo_familiare")
public class NucleoFamiliareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // L'utente che ha creato il nucleo (Amministratore del nucleo)
    @OneToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private UREntity admin;

    // Lista degli utenti registrati che fanno parte del nucleo
    @OneToMany(mappedBy = "nucleoFamiliare")
    @JsonIgnore
    private List<UREntity> membriRegistrati;

    // Lista dei membri aggiunti manualmente (RF-GNF.03)
    @OneToMany(mappedBy = "nucleoFamiliare", cascade = CascadeType.ALL)
    private List<MembroEntity> membriManuali;

    // RF-GNF.09: Lista dei luoghi sicuri/appoggi
    @OneToMany(mappedBy = "nucleoFamiliare", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppoggioEntity> appoggi;
}