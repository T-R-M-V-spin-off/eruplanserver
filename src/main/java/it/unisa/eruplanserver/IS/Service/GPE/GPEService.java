package it.unisa.eruplanserver.IS.Service.GPE;

import it.unisa.eruplanserver.IS.Exception.GPE.GPEException;
import org.json.simple.parser.ParseException;

public interface GPEService {
    /**
     * Genera un piano di evacuazione (RF-GPE.19) e notifica i cittadini (RF-GPE.15).
     *
     * @param nome Nome del piano
     * @param zonaPericolo Dati geometrici della zona di pericolo (JSON)
     * @param zoneSicure Dati geometrici delle zone sicure (JSON)
     * @throws GPEException in caso di errori logici o di validazione
     */
    void generaPiano(String nome, String zonaPericolo, String zoneSicure) throws GPEException;
}