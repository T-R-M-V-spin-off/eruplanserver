package it.unisa.eruplanserver.IS.Entity.GPE;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


public class ZonaSicura {

    private Punto coordinate;
    @Getter
    double raggio;
    public ZonaSicura(Punto coordinate, double raggio) {
        this.coordinate = coordinate;
        this.raggio = raggio;
    }
    public ZonaSicura(){}
}