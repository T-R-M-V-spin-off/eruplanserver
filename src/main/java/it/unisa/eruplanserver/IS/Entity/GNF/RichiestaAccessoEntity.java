package it.unisa.eruplanserver.IS.Entity.GNF;

import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "richiesta_accesso")
public class RichiestaAccessoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nucleo_id", nullable = false)
    private NucleoFamiliareEntity nucleo;

    @ManyToOne
    @JoinColumn(name = "utente_invitato_id", nullable = false)
    private UREntity utenteInvitato;

    @Column(nullable = false)
    private String stato; // "PENDING", "ACCEPTED", "REJECTED"
}