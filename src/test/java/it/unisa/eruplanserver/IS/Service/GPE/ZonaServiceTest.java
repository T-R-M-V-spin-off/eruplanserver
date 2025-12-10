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
    @DisplayName("TC-W-17.1: Numero di punti troppo basso (singolo punto)")
    void testTC_W_17_1_PuntiInsufficienti() {
        // INPUT: ZonaPericolo con un solo punto
        // (40.872507, 14.328918)
        List<Punto> input = Collections.singletonList(
                new Punto(40.872507, 14.328918)
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZonaPericolo(input);
        });

        // OUTPUT ATTESO
        assertEquals("La creazione della zona di pericolo non viene effettuata dato che il campo “ZonaPericolo” è composto da un numero di punti troppo basso.",
                exception.getMessage());
    }

    @Test
    @DisplayName("TC-W-17.2: Ultimo punto non collegato al primo (Poligono non chiuso su se stesso)")
    void testTC_W_17_2_UltimoNonCollegatoAlPrimo() {
        // INPUT:
        // (40.872507, 14.328918) -> P1
        // (40.871469, 14.398270) -> P2
        // (40.830956, 14.392776) -> P3
        // (40.871469, 14.398270) -> P2 (Ripetuto, ma non è P1!)

        List<Punto> input = Arrays.asList(
                new Punto(40.872507, 14.328918),
                new Punto(40.871469, 14.398270),
                new Punto(40.830956, 14.392776),
                new Punto(40.871469, 14.398270)
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZonaPericolo(input);
        });

        // OUTPUT ATTESO
        assertEquals("La creazione della zona di pericolo non viene effettuata dato che nel campo “ZonaPericolo” l’ultimo punto della lista non è collegato al primo.",
                exception.getMessage());
    }

    @Test
    @DisplayName("TC-W-17.3: Primo punto non collegato (Poligono Aperto)")
    void testTC_W_17_3_PrimoPuntoDisconnesso() {
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
        assertEquals("La creazione della zona di pericolo non viene effettuata dato che nel campo “ZonaPericolo” il primo punto non è collegato a nessun altro.", exception.getMessage());
    }


    @Test
    @DisplayName("TC-W-17.4: Creazione della zona di pericolo con successo")
    void testTC_W_17_4_Successo() {
        // INPUT: ZonaPericolo con 4 punti validi e collegati
        List<Punto> input = Arrays.asList(
                new Punto(40.872507, 14.328918), // Punto A
                new Punto(40.871469, 14.398270), // Punto B
                new Punto(40.846022, 14.409943), // Punto C
                new Punto(40.872507, 14.328918)  // Punto A (Chiusura del poligono)
        );

        assertDoesNotThrow(() -> validator.creaZonaPericolo(input));
    }

    @Test
    @DisplayName("TC-W-17.5: Numero di punti troppo basso (due punti)")
    void testTC_W_17_5_UltimoNonCollegatoAlPrimo() {
        // INPUT
        // (40.872507, 14.328918) primo punto
        // (40.871469, 14.398270)
        // (40.846022, 14.409943)
        // (40.830956, 14.392776)
        // (40.871469, 14.398270) diverso dal primo punto

        List<Punto> input = Arrays.asList(
                new Punto(40.872507, 14.328918),
                new Punto(40.871469, 14.398270),
                new Punto(40.846022, 14.409943),
                new Punto(40.830956, 14.392776),
                new Punto(40.871469, 14.398270)
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZonaPericolo(input);
        });
        // OUTPUT ATTESO
        assertEquals("La creazione della zona di pericolo non viene effettuata dato che nel campo “ZonaPericolo” l’ultimo punto della lista non è collegato al primo.", exception.getMessage());
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


