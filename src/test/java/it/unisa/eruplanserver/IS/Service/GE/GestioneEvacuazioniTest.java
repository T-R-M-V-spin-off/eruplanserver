package it.unisa.eruplanserver.IS.Service.GE;

import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Repository.GE.GestioneEvacuazione;
import it.unisa.eruplanserver.IS.Utility.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mysql.cj.conf.PropertyKey.logger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GestioneEvacuazioniTest {

    private static final Logger logger = LoggerFactory.getLogger(GestioneEvacuazioniTest.class);

    @Mock
    private GestioneEvacuazione geRepository;

    @InjectMocks
    private GEService geService;

    @BeforeEach
    void setup() {
        // Inizializza i Mock per prevenire NullPointerException
        MockitoAnnotations.openMocks(this);
        logger.info("========== INIZIO TEST SUITE GESTIONE EVACUAZIONI (GE) ==========");
    }

    /* ===========================
       TC-M-20.1: lunghezza del Codice Fiscale
       =========================== */
    @Test
    void invalidLength() {
        // Codice fiscale con lunghezza != 16
        String cf = "GRT36TGHH53"; // 11 caratteri

        assertFalse(Validator.isCodiceFiscaleLengthValid(cf),
                "Atteso false: lunghezza non valida");
    }

    /* ===========================
       TC-M-20.2: caratteri del Codice Fiscale
       =========================== */
    @Test
    void invalidCharacters() {
        // Codice fiscale lungo 16 ma contiene simboli non ammessi
        String cf = "GRT36T$%GHH5334G";

        assertFalse(Validator.isCodiceFiscaleCharactersValid(cf),
                "Atteso false: caratteri non validi");
    }

        /* ===========================
       TC-M-20.3: caratteri del Codice Fiscale
       =========================== */

    @Test
    @DisplayName("TC-M-20.3: Utente non trovato (Codice Fiscale inesistente)")
    void TC_M_20_3_UtenteNonTrovato() {
        logger.info(">>> Esecuzione TC-M-20.3: Utente non trovato");


        String cfInput = "GRT36T56GHH5334G";
        logger.debug("Input test: CF='{}'", cfInput);

        // Simuliamo che il DB non trovi nulla (ritorna null)
        when(geRepository.findByCodiceFiscale(cfInput)).thenReturn(null);


        Exception exception = assertThrows(Exception.class, () -> {
            geService.segnalaSalvo(cfInput);
        });


        String expectedMessage = "La segnalazione degli utenti arrivati nella zona sicura non viene effettuata dato che il campo “CodiceFiscale” non ha nessuna corrispondenza con un utente sul DB.";
        assertEquals(expectedMessage, exception.getMessage(), "Il messaggio d'errore deve corrispondere al requisito");

        // Verifichiamo che il service abbia effettivamente chiamato il repository
        verify(geRepository, times(1)).findByCodiceFiscale(cfInput);

        logger.info("<<< TC-M-20.3 COMPLETATO: L'eccezione è stata lanciata correttamente.");
    }


       /* ===========================
       TC-M-20.4: Utente trovato nel DB
       =========================== */

    @Test
    @DisplayName("TC-M-20.4: Segnalazione effettuata con successo")
    void TC_M_20_4_Successo() throws Exception {
        logger.info(">>> Esecuzione TC-M-20.4: Successo");


        String cfInput = "RSSMRA80A01H501U";
        UREntity utenteMock = new UREntity();
        utenteMock.setCodiceFiscale(cfInput);

        when(geRepository.findByCodiceFiscale(cfInput)).thenReturn(utenteMock);


        String risultato = geService.segnalaSalvo(cfInput);


        String expectedMessage = "La segnalazione degli utenti arrivati nella zona sicura viene effettuata con successo";
        assertEquals(expectedMessage, risultato);

        verify(geRepository, times(1)).findByCodiceFiscale(cfInput);
        logger.info("<<< TC-M-20.4 COMPLETATO.");
    }
}
