package it.unisa.eruplanserver.IS.Entity.GNF;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CreazioneNucleoRequest {
    private ResidenzaEntity residenza;
    private boolean hasVeicolo;
    private Integer numeroPostiVeicolo;
    public CreazioneNucleoRequest() {}
    public CreazioneNucleoRequest(ResidenzaEntity residenza, boolean hasVeicolo, Integer numeroPostiVeicolo) {
        this.residenza = residenza;
        this.hasVeicolo = hasVeicolo;
        this.numeroPostiVeicolo = numeroPostiVeicolo;
    }
}
