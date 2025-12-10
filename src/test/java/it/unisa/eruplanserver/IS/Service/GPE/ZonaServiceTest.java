package it.unisa.eruplanserver.IS.Service.GPE;

import it.unisa.eruplanserver.IS.Entity.GPE.Punto;
import it.unisa.eruplanserver.IS.Entity.GPE.ZonaSicura;
import it.unisa.eruplanserver.IS.Utility.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ZoneServiceTest {

    // Usiamo @Spy su Validator perché vogliamo testare i suoi metodi reali (creaZoneSicure),
    // ma vogliamo poter fare "stub" del metodo checkConnessione per simulare l'errore geometrico.
    @Spy
    private Validator validator;

    /* ===========================================================================
     * TEST CASE PER UC-W-18: Gestione Zone Sicure
     * (Logica presente in Validator.creaZoneSicure)
     * =========================================================================== */

    @Test
    @DisplayName("TC-W-18.1: Lista zone sicure vuota o nulla")
    void testListaZoneVuota() {
        Exception exceptionNull = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZoneSicure(null);
        });
        assertEquals("Lista vuota", exceptionNull.getMessage());

        Exception exceptionEmpty = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZoneSicure(Collections.emptyList());
        });
        assertEquals("Lista vuota", exceptionEmpty.getMessage());
    }

    @Test
    @DisplayName("TC-W-18.2: Errore singola zona con raggio troppo corto (< 50)")
    void testTC_W_18_2_RaggioTroppoCorto_Singola() {
        List<ZonaSicura> input = Collections.singletonList(
                new ZonaSicura(new Punto(40.872507, 14.328918), 49) // 49 < 50
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZoneSicure(input);
        });

        assertEquals("La creazione della zona sicura non viene effettuata dato che per il campo \"ListaZoneSicure\" per uno dei punti il raggio è troppo corto.",
                exception.getMessage());
    }

    @Test
    @DisplayName("TC-W-18.3: Errore singola zona con raggio troppo grande (> 250)")
    void testTC_W_18_3_RaggioTroppoGrande_Singola() {
        List<ZonaSicura> input = Collections.singletonList(
                new ZonaSicura(new Punto(40.872507, 14.328918), 251) // 251 > 250
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZoneSicure(input);
        });

        assertEquals("La creazione della zona sicura non viene effettuata dato che per il campo \"ListaZoneSicure\" per uno dei punti il raggio è troppo grande.",
                exception.getMessage());
    }

    @Test
    @DisplayName("TC-W-18.8: Errore lista multipla se una zona ha raggio > 250")
    void testTC_W_18_8_RaggioTroppoGrande_Multipla() {
        List<ZonaSicura> input = Arrays.asList(
                new ZonaSicura(new Punto(40.830956, 14.392776), 150),
                new ZonaSicura(new Punto(40.872507, 14.328918), 251) // Questa fallisce
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZoneSicure(input);
        });

        assertEquals("La creazione della zona sicura non viene effettuata dato che per il campo \"ListaZoneSicure\" per uno dei punti il raggio è troppo grande.",
                exception.getMessage());
    }

    @Test
    @DisplayName("TC-W-18.9 - 18.11: Successo con raggi validi (Minimo, Massimo, Intermedio)")
    void testSuccessoRaggiValidi() {
        // Caso raggio minimo (50)
        List<ZonaSicura> inputMin = Arrays.asList(
                new ZonaSicura(new Punto(40.830956, 14.392776), 150),
                new ZonaSicura(new Punto(40.872507, 14.328918), 50)
        );
        assertDoesNotThrow(() -> validator.creaZoneSicure(inputMin));

        // Caso raggio massimo (250)
        List<ZonaSicura> inputMax = Arrays.asList(
                new ZonaSicura(new Punto(40.830956, 14.392776), 150),
                new ZonaSicura(new Punto(40.872507, 14.328918), 250)
        );
        assertDoesNotThrow(() -> validator.creaZoneSicure(inputMax));
    }

    /* ===========================================================================
     * TEST CASE PER UC-W-17: Zona Pericolo
     * (Logica presente in Validator.creaZonaPericolo e Validator.checkConnessione)
     * =========================================================================== */

    @Test
    @DisplayName("TC-W-17.2: Primo punto non collegato (Poligono Aperto)")
    void testTC_W_17_6_PrimoPuntoDisconnesso() {
        // Input: Lista di 4 punti dove il primo (index 0) è DIVERSO dall'ultimo (index 3)
        // Questo viola la condizione (punti.getFirst() != punti.getLast()) in checkConnessione
        List<Punto> input = Arrays.asList(
                new Punto(40.872507, 14.328918), // Punto A
                new Punto(40.871469, 14.398270), // Punto B
                new Punto(40.846022, 14.409943), // Punto C
                new Punto(40.830956, 14.392776)  // Punto D (Diverso da A -> Poligono aperto)
        );


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZonaPericolo(input);
        });

        // Verifichiamo il messaggio reale lanciato da Validator.checkConnessione
        assertEquals("Il poligono non è chiuso", exception.getMessage());
    }

    @Test
    @DisplayName("TC-W-17.6: Poligono non chiuso (Punti duplicati errati)")
    void testPoligonoConDuplicati() {
        // Input: 5 punti totali, ma solo 3 unici.
        // A -> B -> A -> C -> A

        Punto p1 = new Punto(40.872507, 14.328918);
        Punto p2 = new Punto(40.830956, 14.392776);
        Punto p3 = new Punto(40.846022, 14.409943);
        Punto p4 = new Punto(40.830956, 14.392776);
        Punto p5 = new Punto(40.811730, 14.350204);

        List<Punto> input = Arrays.asList(p1, p2, p3, p4, p2);

        // La logica (set.size() < punti.size()) sarà vera
        // Lancerà "Il poligono non è chiuso"

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZonaPericolo(input);
        });

        assertEquals("La forma del poligono non è valida", exception.getMessage());
    }
}

