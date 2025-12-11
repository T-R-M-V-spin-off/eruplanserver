package it.unisa.eruplanserver.IS.Service.GPE;

import it.unisa.eruplanserver.IS.Entity.GPE.Punto;
import it.unisa.eruplanserver.IS.Entity.GPE.ZonaSicura;
import it.unisa.eruplanserver.IS.Entity.GPE.ZonaSicuraEntity;
import it.unisa.eruplanserver.IS.Utility.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ZoneServiceTest {

    @Spy
    private Validator validator;

    @InjectMocks
    private ZonaSicuraServiceImpl zonaSicuraService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /* =======================================================================
       UC-W-18: Gestione Zone Sicure
       ======================================================================= */

    @Test
    @DisplayName("TC-W-18.1: Lista zone sicure nulla o senza coordinate")
    void testTC_W_18_1_ListaZoneSicureNulla() {
        // Lista nulla
        Exception exceptionNull = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZoneSicure(null);
        });
        assertEquals(
                "La creazione della zona sicura non viene effettuata dato che per il campo “ListaZoneSicure” non è stata definita nessuna coordinata.",
                exceptionNull.getMessage()
        );

        // Lista vuota
        Exception exceptionEmpty = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZoneSicure(Collections.emptyList());
        });
        assertEquals(
                "La creazione della zona sicura non viene effettuata dato che per il campo “ListaZoneSicure” non è stata definita nessuna coordinata.",
                exceptionEmpty.getMessage()
        );
    }

    @Test
    @DisplayName("TC-W-18.2: Errore singola zona con raggio troppo corto (< 50)")
    void testTC_W_18_2_RaggioTroppoCorto_Singola() {
        List<ZonaSicura> input = Collections.singletonList(
                new ZonaSicura(new Punto(40.872507, 14.328918), 49)
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZoneSicure(input);
        });

        assertEquals(
                "La creazione della zona sicura non viene effettuata dato che per il campo \"ListaZoneSicure\" per uno dei punti il raggio è troppo corto.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("TC-W-18.3: Errore singola zona con raggio troppo grande (> 250)")
    void testTC_W_18_3_RaggioTroppoGrande_Singola() {
        List<ZonaSicura> input = Collections.singletonList(
                new ZonaSicura(new Punto(40.872507, 14.328918), 251)
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZoneSicure(input);
        });

        assertEquals(
                "La creazione della zona sicura non viene effettuata dato che per il campo \"ListaZoneSicure\" per uno dei punti il raggio è troppo grande.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("TC-W-18.7: Raggio troppo piccolo in almeno una zona della lista")
    void testTC_W_18_7_RaggioTroppoPiccolo_Multipla() {
        // Lista con due zone sicure: una valida, una con raggio < 50
        ZonaSicuraEntity validZona = new ZonaSicuraEntity(40.830956, 14.392776, 150);
        ZonaSicuraEntity smallRaggioZona = new ZonaSicuraEntity(40.872507, 14.328918, 49);

        List<ZonaSicuraEntity> lista = Arrays.asList(validZona, smallRaggioZona);

        Exception ex = assertThrows(Exception.class, () ->
                zonaSicuraService.creaZonaSicura(lista)
        );

        assertEquals(
                "La creazione della zona sicura non viene effettuata dato che per il campo “ListaZoneSicure” per uno dei punti il raggio è troppo corto.",
                ex.getMessage()
        );
    }

    @Test
    @DisplayName("TC-W-18.8: Errore lista multipla se una zona ha raggio > 250")
    void testTC_W_18_8_RaggioTroppoGrande_Multipla() {
        List<ZonaSicura> input = Arrays.asList(
                new ZonaSicura(new Punto(40.830956, 14.392776), 150),
                new ZonaSicura(new Punto(40.872507, 14.328918), 251)
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZoneSicure(input);
        });

        assertEquals(
                "La creazione della zona sicura non viene effettuata dato che per il campo \"ListaZoneSicure\" per uno dei punti il raggio è troppo grande.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("TC-W-18.9 - 18.11: Successo con raggi validi")
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

    /* =======================================================================
       UC-W-17: Zona Pericolo
       ======================================================================= */

    @Test
    @DisplayName("TC-W-17.1: Numero di punti troppo basso (singolo punto)")
    void testTC_W_17_1_PuntiInsufficienti() {
        List<Punto> input = Collections.singletonList(new Punto(40.872507, 14.328918));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZonaPericolo(input);
        });

        assertEquals(
                "La creazione della zona di pericolo non viene effettuata dato che il campo “ZonaPericolo” è composto da un numero di punti troppo basso.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("TC-W-17.2: Ultimo punto non collegato al primo")
    void testTC_W_17_2_UltimoNonCollegatoAlPrimo() {
        List<Punto> input = Arrays.asList(
                new Punto(40.872507, 14.328918),
                new Punto(40.871469, 14.398270),
                new Punto(40.830956, 14.392776),
                new Punto(40.871469, 14.398270)
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZonaPericolo(input);
        });

        assertEquals(
                "La creazione della zona di pericolo non viene effettuata dato che nel campo “ZonaPericolo” l’ultimo punto della lista non è collegato al primo.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("TC-W-17.3: Primo punto non collegato")
    void testTC_W_17_3_PrimoPuntoDisconnesso() {
        List<Punto> input = Arrays.asList(
                new Punto(40.872507, 14.328918),
                new Punto(40.871469, 14.398270),
                new Punto(40.846022, 14.409943),
                new Punto(40.830956, 14.392776)
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZonaPericolo(input);
        });

        assertEquals(
                "La creazione della zona di pericolo non viene effettuata dato che nel campo “ZonaPericolo” il primo punto non è collegato a nessun altro.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("TC-W-17.4: Creazione della zona di pericolo con successo")
    void testTC_W_17_4_Successo() {
        List<Punto> input = Arrays.asList(
                new Punto(40.872507, 14.328918),
                new Punto(40.871469, 14.398270),
                new Punto(40.846022, 14.409943),
                new Punto(40.872507, 14.328918)
        );

        assertDoesNotThrow(() -> validator.creaZonaPericolo(input));
    }

    @Test
    @DisplayName("TC-W-17.5: Ultimo punto non collegato al primo (duplicati errati)")
    void testTC_W_17_5_UltimoNonCollegatoAlPrimo() {
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

        assertEquals(
                "La creazione della zona di pericolo non viene effettuata dato che nel campo “ZonaPericolo” l’ultimo punto della lista non è collegato al primo.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("TC-W-17.6: Poligono non chiuso (duplicati errati)")
    void testPoligonoConDuplicati() {
        Punto p1 = new Punto(40.872507, 14.328918);
        Punto p2 = new Punto(40.830956, 14.392776);
        Punto p3 = new Punto(40.846022, 14.409943);
        Punto p4 = new Punto(40.830956, 14.392776);
        Punto p5 = new Punto(40.811730, 14.350204);

        List<Punto> input = Arrays.asList(p1, p2, p3, p4, p2);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZonaPericolo(input);
        });

        assertEquals("La forma del poligono non è valida", exception.getMessage());
    }
}
