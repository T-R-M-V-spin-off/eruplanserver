package it.unisa.eruplanserver.IS.Service.GPE;

import it.unisa.eruplanserver.IS.Entity.GPE.PianoEvacuazioneEntity;
import it.unisa.eruplanserver.IS.Entity.GPE.Punto;
import it.unisa.eruplanserver.IS.Entity.GPE.ZonaPericolo;
import it.unisa.eruplanserver.IS.Entity.GPE.ZonaSicura;
import it.unisa.eruplanserver.IS.Utility.Validator;
import it.unisa.eruplanserver.IS.Repository.GPE.GPERepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ZonaServiceTest
 *
 * Include:
 * - TC-W-18.*: Test Entity e Validazione Zone Sicure
 * - TC-W-17.*: Validazione Zona Pericolo
 * - TC-W-17.7: Test integrazione GPEService (Salvataggio + Notifica)
 */
@ExtendWith(MockitoExtension.class)
class ZonaServiceTest {

    // =========================
    // Componenti Mock/Spy
    // =========================

    @Spy
    private Validator validator;

    // Service per la gestione specifica delle zone sicure (se presente nel tuo design)
    @InjectMocks
    private ZonaSicuraServiceImpl zonaSicuraService;

    @Mock
    private GPERepository gpeRepository;

    @Mock
    private FirebaseService firebaseService;

    // Service principale GPE
    @InjectMocks
    private GPEServiceImpl gpeService;

    @BeforeEach
    void init() {
        // Configurazione base per i test che coinvolgono il repository
        // Usiamo lenient() perché non tutti i test di questa classe chiamano il repository
        lenient().when(gpeRepository.save(any(PianoEvacuazioneEntity.class)))
                .thenAnswer(inv -> {
                    PianoEvacuazioneEntity p = inv.getArgument(0);
                    p.setId(999L); // Simuliamo ID generato dal DB
                    return p;
                });

        lenient().when(gpeRepository.existsByNome(anyString())).thenReturn(false);
    }

    /* =======================================================================
       SEZIONE 1: TC-W-18 - GESTIONE ZONE SICURE
       ======================================================================= */

    // --- NUOVO TEST INTEGRATO ---
    @Test
    @DisplayName("TC-W-18.5: Creazione Zona Sicura con Successo (Entity Test)")
    void testTC_W_18_5_CreazioneZonaSicura_Successo() {
        // INPUT
        double latitudine = 40.872507;
        double longitudine = 14.328918;
        double raggio = 250.0;

        // ACT
        Punto punto = new Punto(latitudine, longitudine);
        ZonaSicura zonaSicura = new ZonaSicura(punto, raggio);

        // ASSERT (OUTPUT)
        assertNotNull(zonaSicura, "L'oggetto ZonaSicura non dovrebbe essere null");
        assertEquals(latitudine, zonaSicura.getCoordinate().getLatitudine());
        assertEquals(longitudine, zonaSicura.getCoordinate().getLongitudine());
        assertEquals(raggio, zonaSicura.getRaggio());
        
        // Output da specifica: "La creazione della zona sicura viene effettuata con successo."
    }

    @Test
    @DisplayName("TC-W-18.1: Lista zone sicure nulla o vuota")
    void testTC_W_18_1_ListaZoneSicureNulla() {
        // Caso Null
        Exception exceptionNull = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZoneSicure(null);
        });
        assertEquals(
                "La creazione della zona sicura non viene effettuata dato che per il campo \"ListaZoneSicure\" non è stata definita nessuna coordinata.",
                exceptionNull.getMessage()
        );

        // Caso Empty
        Exception exceptionEmpty = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZoneSicure(Collections.emptyList());
        });
        assertEquals(
                "La creazione della zona sicura non viene effettuata dato che per il campo \"ListaZoneSicure\" non è stata definita nessuna coordinata.",
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
        assertTrue(exception.getMessage().contains("raggio è troppo corto"));
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
        assertTrue(exception.getMessage().contains("raggio è troppo grande"));
    }

    @Test
    @DisplayName("TC-W-18.7: Raggio troppo piccolo in almeno una zona della lista")
    void testTC_W_18_7_RaggioTroppoPiccolo_Multipla() {
        // Uso ZonaSicura standard (correzione per uniformità)
        ZonaSicura validZona = new ZonaSicura(new Punto(40.830956, 14.392776), 150);
        ZonaSicura smallRaggioZona = new ZonaSicura(new Punto(40.872507, 14.328918), 49);

        List<ZonaSicura> lista = Arrays.asList(validZona, smallRaggioZona);

        // Assumiamo che zonaSicuraService chiami internamente il validator o faccia check simili
        // Se zonaSicuraService non implementa logica, usiamo validator direttamente per il test
        Exception ex = assertThrows(Exception.class, () ->
            validator.creaZoneSicure(lista) 
            // O zonaSicuraService.creaZonaSicura(lista) se implementato
        );

        assertTrue(ex.getMessage().contains("raggio è troppo corto"));
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
        assertTrue(exception.getMessage().contains("raggio è troppo grande"));
    }

    @Test
    @DisplayName("TC-W-18.9 - 18.11: Successo con raggi validi (Limiti)")
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
       SEZIONE 2: TC-W-17 - ZONA PERICOLO (Validator Tests)
       ======================================================================= */

    @Test
    @DisplayName("TC-W-17.1: Numero di punti troppo basso")
    void testTC_W_17_1_PuntiInsufficienti() {
        List<Punto> input = Collections.singletonList(new Punto(40.872507, 14.328918));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZonaPericolo(input);
        });
        assertTrue(exception.getMessage().contains("numero di punti troppo basso"));
    }

    @Test
    @DisplayName("TC-W-17.2: Ultimo punto non collegato al primo")
    void testTC_W_17_2_UltimoNonCollegatoAlPrimo() {
        List<Punto> input = Arrays.asList(
                new Punto(40.872507, 14.328918),
                new Punto(40.871469, 14.398270),
                new Punto(40.830956, 14.392776),
                new Punto(40.871469, 14.398270) // Diverso dal primo
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZonaPericolo(input);
        });
        assertTrue(exception.getMessage().contains("ultimo punto della lista non è collegato al primo"));
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
        // Nota: Il messaggio d'errore dipende dalla logica esatta del validator, 
        // qui assumiamo controlli la chiusura.
        assertNotNull(exception);
    }

    @Test
    @DisplayName("TC-W-17.4: Creazione della zona di pericolo con successo")
    void testTC_W_17_4_Successo() {
        List<Punto> input = Arrays.asList(
                new Punto(40.872507, 14.328918),
                new Punto(40.871469, 14.398270),
                new Punto(40.846022, 14.409943),
                new Punto(40.872507, 14.328918) // Chiuso correttamente
        );
        assertDoesNotThrow(() -> validator.creaZonaPericolo(input));
    }

    @Test
    @DisplayName("TC-W-17.6: Poligono non valido (incroci/duplicati errati)")
    void testPoligonoConDuplicatiErrati() {
        Punto p1 = new Punto(40.872507, 14.328918);
        Punto p2 = new Punto(40.830956, 14.392776);
        Punto p3 = new Punto(40.846022, 14.409943);
        
        // Poligono che si incrocia o ha punti ripetuti male
        List<Punto> input = Arrays.asList(p1, p2, p3, p2, p1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.creaZonaPericolo(input);
        });
        // Verifica generica se il messaggio contiene errore di forma
        assertNotNull(exception);
    }

    /* =======================================================================
       SEZIONE 3: TC-W-17.7 - INTEGRAZIONE GPE SERVICE
       ======================================================================= */

    @Test
    @DisplayName("TC-W-17.7: GPEServiceImpl - crea zona, salva e notifica")
    void tcW17_7_zonaPericolo_chiusa_salva_e_notifica() throws Exception {
        // ARRANGE
        Punto a = new Punto(40.872507, 14.328918);
        Punto b = new Punto(40.871469, 14.398270);
        Punto c = new Punto(40.846022, 14.409943);
        Punto d = new Punto(40.830956, 14.392776);
        List<Punto> punti = Arrays.asList(a, b, c, d, a); // chiuso: primo == ultimo

        ZonaPericolo zonaPericolo = new ZonaPericolo();
        zonaPericolo.setPunti(punti);

        String nomePiano = "PianoTestOK";
        
        // ACT
        // Passiamo null come ZonaSicura perché il test si concentra sulla ZonaPericolo e integrazione
        gpeService.generaPiano(nomePiano, zonaPericolo, (ZonaSicura) null);

        // ASSERT & VERIFY
        
        // 1. Catturiamo l'entità salvata
        ArgumentCaptor<PianoEvacuazioneEntity> captor = ArgumentCaptor.forClass(PianoEvacuazioneEntity.class);
        verify(gpeRepository, times(1)).save(captor.capture());
        PianoEvacuazioneEntity saved = captor.getValue();

        // 2. Verifichiamo i dati
        assertEquals(zonaPericolo, saved.getZonaPericolo());
        assertEquals(nomePiano, saved.getNome());
        assertNotNull(saved.getDataCreazione());

        // 3. Verifichiamo la notifica
        verify(firebaseService, times(1)).inviaNotificaBroadcast(
            anyString(), 
            anyString(), 
            eq("emergenza") // Verifica che il topic sia corretto
        );
    }
}
