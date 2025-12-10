package it.unisa.eruplanserver.IS.Entity.GPE;

import java.util.Objects;

public class Punto {
    double lat, lon;

    public Punto(){}
    public Punto(double lat, double lon) { this.lat = lat; this.lon = lon; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Punto)) return false;
        Punto punto = (Punto) o;
        return Double.compare(punto.lat, lat) == 0 && Double.compare(punto.lon, lon) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
