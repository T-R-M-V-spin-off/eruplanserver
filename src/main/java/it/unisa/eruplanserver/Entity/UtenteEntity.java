package it.unisa.eruplanserver.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="utente")
public class UtenteEntity {

    @Id
    @Column(name = "email")
    private String email;
    private String password;
    private String nome;

    // Relazione OneToOne con Profilo
    @OneToOne(mappedBy = "utente", cascade = CascadeType.REMOVE)
    private ProfiloEntity profilo;

    // Relazione OneToMany con Ordine
    @OneToMany(mappedBy = "utente", cascade = CascadeType.REMOVE)
    private List<OrdineEntity> ordini;
}
