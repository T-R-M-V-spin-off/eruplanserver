package it.unisa.eruplanserver.IS.Entity.GNF;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class AggiornaVeicoloRequest {
    private boolean hasVeicolo;
    private Integer numeroPostiVeicolo;
    public AggiornaVeicoloRequest() {}
    public AggiornaVeicoloRequest(boolean hasVeicolo, Integer numeroPostiVeicolo) {
        this.hasVeicolo = hasVeicolo;
        this.numeroPostiVeicolo = numeroPostiVeicolo;
    }


}
