package it.unisa.eruplanserver.IS.Entity.GPE;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class ZonaPericolo {
    @Id
    private Long id;
    List<Punto> punti;
}