package it.unisa.eruplanserver.IS.Service.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.ResidenzaEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.NucleoFamiliareEntity;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Repository.GNF.ResidenzaRepository;
import it.unisa.eruplanserver.IS.Repository.GNF.NucleoFamiliareRepository;
import it.unisa.eruplanserver.IS.Repository.GUM.URRepository;
import it.unisa.eruplanserver.IS.Exception.GNF.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test Suite per Validazione Indirizzo/Residenza
 * 
 * Questa suite testa tutte le validazioni del campo "via/piazza"
 * secondo le specifiche di validazione indirizzi
 * 
 * Test Cases:
 * - TC_M_08_1: Via/Piazza troppo corta (< 1 carattere)
 * - TC_M_08_2: Via/Piazza troppo lunga (> 40 caratteri)
 * - TC_M_08_3: Via/Piazza valida
 */
@DisplayName("Test Suite: Validazione Indirizzo - Via/Piazza")
class IndirizzoServiceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(IndirizzoServiceTest.class);

    @Mock
    private ResidenzaRepository residenzaRepository;

        @Mock
        private URRepository urRepository;

    @InjectMocks
    private GNFServiceImpl service;

    @Mock
    private NucleoFamiliareRepository nucleoFamiliareRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logger.info("========== INIZIO TEST SUITE VALIDAZIONE INDIRIZZO ==========");
                // Prevent NPE: stub URRepository so creaNucleoFamiliare can proceed to address validation
                when(urRepository.findByCodiceFiscale("CF123")).thenReturn(UREntity.builder().codiceFiscale("CF123").build());
    }

    /**
     * TC_M_08_1: Validazione Via Troppo Corta
     * 
     * Scenario: Utente tenta di aggiungere indirizzo con via/piazza vuota o troppo corta
     * Input: via = "" (stringa vuota)
     * Expected: ValidationException con messaggio "Nome via/piazza troppo corto"
     * Verifiche:
     *   - Eccezione lanciata
     *   - Messaggio d'errore corretto
     *   - Indirizzo NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_08_1: Via/Piazza troppo corta")
    void testViaTroppoCorta_TC_M_08_1() {
        logger.info(">>> Esecuzione TC_M_08_1: Validazione via troppo corta");
        
        // ARRANGE
        ResidenzaEntity input = ResidenzaEntity.builder()
                .viaPiazza("")  // ❌ Via vuota
                .civico("12")
                .comune("Napoli")
                .cap("80100")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Italia")
                .build();
        logger.debug("Residenza creata con via vuota: via='{}' (lunghezza=0)", input.getViaPiazza());

        // ACT & ASSERT
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.creaNucleoFamiliare("CF123", input, false, 0)
        );

        assertEquals("Nome via/piazza troppo corto", ex.getMessage(),
                "Il messaggio d'errore deve indicare via troppo corta");
        
        // VERIFY
        verify(residenzaRepository, never()).save(any());
        
        logger.info("<<< TC_M_08_1 COMPLETATO: Validazione via corta funziona correttamente");
    }

    /**
     * TC_M_08_2: Validazione Via Troppo Lunga
     * 
     * Scenario: Utente tenta di aggiungere indirizzo con via/piazza troppo lunga
     * Input: via = 41 caratteri (limite massimo è 40)
     * Expected: ValidationException con messaggio "Nome via/piazza troppo lungo"
     * Verifiche:
     *   - Eccezione lanciata
     *   - Messaggio d'errore corretto
     *   - Indirizzo NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_08_2: Via/Piazza troppo lunga")
    void testViaTroppoLunga_TC_M_08_2() {
        logger.info(">>> Esecuzione TC_M_08_2: Validazione via troppo lunga");
        
        // ARRANGE
        String viaLunga = "A".repeat(41);  // 41 caratteri (oltre il limite)
        ResidenzaEntity input = ResidenzaEntity.builder()
                .viaPiazza(viaLunga)
                .civico("12")
                .comune("Napoli")
                .cap("80100")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Italia")
                .build();
        logger.debug("Residenza creata con via troppo lunga: lunghezza={}", input.getViaPiazza().length());

        // ACT & ASSERT
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.creaNucleoFamiliare("CF123", input, false, 0)
        );

        assertEquals("Nome via/piazza troppo lungo", ex.getMessage(),
                "Il messaggio d'errore deve indicare via troppo lunga");
        
        // VERIFY
        verify(residenzaRepository, never()).save(any());
        logger.info("<<< TC_M_08_2 COMPLETATO: Validazione via lunga funziona correttamente");
    }
    // =================================================================================
    //               TEST VIA/PIAZZA CARATTERI (TC-M-08.3)
    // =================================================================================
    /**
     * TC_M_08.3:Via/Piazza con caratteri non validi
     * Scenario: Utente tenta di creare nucleo familiare con via/piazza contenente caratteri non consentiti
     * Input: via= "Via Sarti%" (contiene un carattere speciale non consentito)
     * Expected: ValidationException con messaggio specifico per caratteri non validi in via/piazza
     * Verifiche:
     * - Eccezione lanciata di tipo ValidationException
     * - Messaggio d'errore corrisponde esattamente a quello definito nel Service
     * - Indirizzo NON salvato nel repository delle residenze
     */
    @Test
    @DisplayName("TC_M_08_3: Via/Piazza con caratteri non validi")
    void testViaCaratteriNonValidi_TC_M_08_3() {
        logger.info(">>>Esecuzione TCM_M_08_3: Validazione via caratteri non validi");
        //ARRANGE
        ResidenzaEntity input=ResidenzaEntity.builder()
                .viaPiazza("Via Sarti%")
                .civico("675")
                .comune("Pompei")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno")
                .build();
        logger.debug("Residenza creata con caratteri non validi: via='{}'", input.getViaPiazza());

        //ACT & ASSERT
        ValidationException ex=assertThrows(
                ValidationException.class,
                ()-> service.creaNucleoFamiliare("CF123", input, false, null)
        );
        assertEquals("La creazione del nucleo familiare non viene effettuata dato che il campo \"Via/Piazza\" contiene caratteri non validi",
                ex.getMessage());

        //VERIFY
        verify(residenzaRepository, never()).save(any());
        logger.info("<<<TC_M_08_3 COMPLETATO");
    }
    // =================================================================================
    //               TEST CIVICO (TC-M-08.4, TC-M-08.5, TC-M-08.6)
    // =================================================================================
    /**
     * TC_M_08_4: Validazione Civico Troppo Corto
     * Scenario: Utente tenta di creare nucleo familiare con civico vuoto
     * Input: civico = "" (stringa vuota, lunghezza 0)
     * Expected: ValidationException con messaggio specifico per civico troppo corto
     * Verifiche:
     *- Eccezione lanciata prima della validazione generica dell'indirizzo
     *- Messaggio d'errore indica lunghezza insufficiente del civico
     *- Sistema non procede al salvataggio dei dati
     */
    @Test
    @DisplayName("TC_M_08_4: Civico troppo corto")
    void testCivicoTroppoCorto_TC_M_08_4() {
        logger.info(">>>Esecuzione TC_m_08_4: Validazione civico troppo corto");
        ResidenzaEntity input= ResidenzaEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("")
                .comune("Pompei")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno")
                .build();
        ValidationException ex= assertThrows(ValidationException.class,() ->
                service.creaNucleoFamiliare("CF123", input, false, null)
        );
        assertEquals("La creazione del nucleo familiare non viene effettuata dato che il campo \"Civico\" è troppo corto",
        ex.getMessage());
        //VERIFY
        verify(residenzaRepository, never()).save(any());

        logger.info(">>>TC_M_08_4 COMPLETATO");
    }
    /**
     * TC_M_08_5: Validazione Civico Troppo Lungo
     * Scenario: Utente tenta di creare nucleo familiare con civico superiore a 6 caratteri
     *  Input: civico = "1234567" ( qui sono 7 caratteri,il limite massimo è 6)
     *  Expected: ValidationException con messaggio specifico per civico troppo lungo
     *  Verifiche:
     * - Eccezione lanciata durante validazione specifica del civico
     * - Messaggio d'errore indica superamento della lunghezza massima consentita
     * - Validazione avviene prima della chiamata a Validator.isIndirizzoValid
     */
    @Test
    @DisplayName("TC_M_05: Civico troppo lungo")
    void testCivicoTroppoLungo_TC_M_05() {
        logger.info(">>>Esecuzione TC_M_05: Validazione civico troppo lungo");
        ResidenzaEntity input= ResidenzaEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("1234567")
                .comune("Pompei")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno")
                .build();
        ValidationException ex= assertThrows(ValidationException.class,() ->
                service.creaNucleoFamiliare("CF123", input, false, null)
        );
        assertEquals("La creazione del nucleo familiare non viene effettuata dato che il campo \"Civico\" è troppo lungo",
                ex.getMessage());

        //VERIFY
        verify(residenzaRepository, never()).save(any());
        logger.info(">>>TC_M_05 COMPLETATO");
    }
    /**TC_M_08_6: Validazione Civico con Caratteri Non Validi
     *Scenario: Utente tenta di creare nucleo familiare con civico contenente caratteri non consentiti
     * Input: civico = "67-58" (contiene il carattere '-', non permesso dalla regex)
     * Expected: ValidationException con messaggio specifico per caratteri non validi nel civico
     * Verifiche:
     *  - Eccezione lanciata durante validazione pattern del civico
     *  - Messaggio d'errore indica presenza di caratteri non validi
     *  - Regex "^[0-9a-zA-Z/\\s]+$" non accetta il trattino come carattere valido
     */
    @Test
    @DisplayName("TC_M_08_6:Civico con caratteri non validi")
    void testCivicoConCaratteriNonValidi_TC_M_08_6() {
        logger.info(">>>Esecuzione TC_M_08_6: Validazione civico con caratteri non validi");
        ResidenzaEntity input = ResidenzaEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("67-68")
                .comune("Pompei")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno")
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () ->
                service.creaNucleoFamiliare("CF123", input, false, null)
        );
        assertEquals("La creazione del nucleo familiare non viene effettuata dato che il campo \"Civico\" contiene caratteri non validi",
                ex.getMessage());

        //VERIFY
        verify(residenzaRepository, never()).save(any());
        logger.info(">>>TC_M_08_6 COMPLETATO");
    }
// =================================================================================
//               TEST COMUNE (TC-M-08.7, TC-M-08.8, TC-M-08.9)
// =================================================================================

    /**TC_M_08_7: Validazione Comune Troppo Corto
     *Scenario: Utente tenta di creare nucleo familiare con comune inferiore a 2 caratteri
     * Input: comune = "P" (1 carattere, mentre il limite minimo è 2)
     * Expected: ValidationException con messaggio specifico per comune troppo corto
     * Verifiche:
     * - Eccezione lanciata durante validazione lunghezza minima del comune
     * - Messaggio d'errore indica lunghezza insufficiente
     * - Validazione avviene dopo il trim() della stringa
     */
    @Test
    @DisplayName("TC_M_08_7: Comune troppo corto")
    void testComuneTroppoCorto_TC_M_08_7() {
        logger.info(">>>Esecuzione TC_M_08_7: Validazione comune troppo corto");
        ResidenzaEntity input = ResidenzaEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("67-68")
                .comune("P")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno")
                .build();
        ValidationException ex = assertThrows(ValidationException.class, () ->
                service.creaNucleoFamiliare("CF123", input, false, null)
        );
        assertEquals("La creazione del nucleo familiare non viene effettuata dato che il campo \"Comune\" è troppo corto",
                ex.getMessage());

        //VERIFY
        verify(residenzaRepository, never()).save(any());
        logger.info(">>>TC_M_08_7 COMPLETATO");
    }
        /**
         * TC_M_08_8: Validazione Comune Troppo Lungo
         * Scenario: Utente tenta di creare nucleo familiare con comune superiore a 40 caratteri
         * Input: comune = 41 caratteri 'A' (limite massimo è 40)
         * Expected: ValidationException con messaggio specifico per comune troppo lungo
         * Verifiche:
         * - Eccezione lanciata durante validazione lunghezza massima del comune
         * - Messaggio d'errore indica superamento della lunghezza consentita
         * - Test verifica il boundary case (41 vs 40 caratteri)
         */
        @Test
        @DisplayName("TC_M_08_8: Comune troppo lungo")
        void testComuneTroppoLungo_TC_M_08_8(){
            logger.info(">>>EsecuzioneTC_M_08_8: Validazione comune troppo lungo");
            String lungoComune="A".repeat(41);
            ResidenzaEntity input= ResidenzaEntity.builder()
                    .viaPiazza("Via Sarti")
                    .civico("67-68")
                    .comune(lungoComune)
                    .cap("67489")
                    .provincia("Napoli")
                    .regione("Campania")
                    .paese("Messigno")
                    .build();
            ValidationException ex=assertThrows(ValidationException.class,()->
                    service.creaNucleoFamiliare("CF123", input, false, null)
            );
            assertEquals("La creazione del nucleo familiare non viene effettuata datp che il campo \"Comune\" è troppo lungo",
                    ex.getMessage());

            //VERIFY
            verify(residenzaRepository, never()).save(any());
            logger.info(">>>TC_M_08_8 COMPLETATO");

        }
    /**
     * TC_M_08_9: Validazione Comune con Caratteri Non Validi
     * Scenario: Utente tenta di creare nucleo familiare con comune contenente numeri
     * Input: comune = "Pomp3i" (contiene il numero '3')
     * Expected: ValidationException con messaggio specifico per caratteri non validi nel comune
     * Verifiche:
     * - Eccezione lanciata durante validazione pattern del comune
     * - Messaggio d'errore indica presenza di caratteri non validi
     *  - Regex "^\p{L}+$" accetta solo lettere (inclusi caratteri Unicode/lettere accentate)
     *   - I numeri non sono considerati caratteri validi per il comune
     */
    @Test
    @DisplayName("TC_M_08_9: Comune con caratteri non validi")
    void testComuneConCaratteriNonValidi_TC_M_08_9() {
        logger.info("Esecuzione TC_M_08_9: Validazione con caratteri non validi");
        ResidenzaEntity input= ResidenzaEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("67-68")
                .comune("Pomp3i")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno")
                .build();
        ValidationException ex=assertThrows(ValidationException.class,()->
                service.creaNucleoFamiliare("CF123", input, false, null)
        );
        assertEquals("La creazione del nucleo familiare non viene effettuata dato che il campo \"Comune\" contiene caratteri non validi",
                ex.getMessage());

        //VERIFY
        verify(residenzaRepository, never()).save(any());
        logger.info(">>>TC_M_08_9 COMPLETATO");
    }


// =================================================================================
    //               TEST CAP (TC-M-08.10 - TC-M-08.13)
    // =================================================================================

    /**
     * TC_M_08_10: Validazione CAP Troppo Corto
     * Scenario: Utente inserisce CAP con meno di 5 cifre
     * Input: cap = "8408"
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC_M_08_10: CAP troppo corto")
    void testCapTroppoCorto_TC_M_08_10() {
        logger.info(">>> Esecuzione TC_M_08_10: Validazione CAP corto");
        ResidenzaEntity input = ResidenzaEntity.builder()
                .viaPiazza("Via Roma").civico("10").comune("Napoli")
                .cap("8408")
                .provincia("Napoli").regione("Campania").paese("Italia")
                .build();
        Exception ex = assertThrows(Exception.class, () ->
                service.creaNucleoFamiliare("CF123", input, false, 0)
        );
        logger.debug("Eccezione catturata: {}", ex.getMessage());
        verify(residenzaRepository, never()).save(any());
        logger.info("<<< TC_M_08_10 COMPLETATO");
    }

    /**
     * TC_M_08_11: Validazione CAP Troppo Lungo
     * Scenario: Utente inserisce CAP con più di 5 cifre
     * Input: cap = "840844"
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC_M_08_11: CAP troppo lungo")
    void testCapTroppoLungo_TC_M_08_11() {
        logger.info(">>> Esecuzione TC_M_08_11: Validazione CAP lungo");
        ResidenzaEntity input = ResidenzaEntity.builder()
                .viaPiazza("Via Roma").civico("10").comune("Napoli")
                .cap("840844")
                .provincia("Napoli").regione("Campania").paese("Italia")
                .build();
        Exception ex = assertThrows(Exception.class, () ->
                service.creaNucleoFamiliare("CF123", input, false, 0)
        );
        verify(residenzaRepository, never()).save(any());
        logger.info("<<< TC_M_08_11 COMPLETATO");
    }

    /**
     * TC_M_08_12: Validazione CAP Formato Errato (Lettere)
     * Scenario: Utente inserisce lettere nel CAP
     * Input: cap = "8408A"
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC_M_08_12: CAP con lettere")
    void testCapConLettere_TC_M_08_12() {
        logger.info(">>> Esecuzione TC_M_08_12: Validazione CAP caratteri invalidi");
        ResidenzaEntity input = ResidenzaEntity.builder()
                .viaPiazza("Via Roma").civico("10").comune("Napoli")
                .cap("8408A")
                .provincia("Napoli").regione("Campania").paese("Italia")
                .build();
        Exception ex = assertThrows(Exception.class, () ->
                service.creaNucleoFamiliare("CF123", input, false, 0)
        );

        verify(residenzaRepository, never()).save(any());
        logger.info("<<< TC_M_08_12 COMPLETATO");
    }

    /**
     * TC_M_08_13: Validazione CAP Formato Errato (Simboli)
     * Scenario: Utente inserisce simboli nel CAP
     * Input: cap = "8408@"
     * Expected: ValidationException
     */
    @Test
    @DisplayName("TC_M_08_13: CAP con simboli")
    void testCapConSimboli_TC_M_08_13() {
        logger.info(">>> Esecuzione TC_M_08_13: Validazione CAP simboli");
        ResidenzaEntity input = ResidenzaEntity.builder()
                .viaPiazza("Via Roma").civico("10").comune("Napoli")
                .cap("8408@")
                .provincia("Napoli").regione("Campania").paese("Italia")
                .build();
        Exception ex = assertThrows(Exception.class, () ->
                service.creaNucleoFamiliare("CF123", input, false, 0)
        );

        verify(residenzaRepository, never()).save(any());
        logger.info("<<< TC_M_08_13 COMPLETATO");
    }

    // =================================================================================
    //               TEST  PROVINCIA (TC-M-08.14 - TC-M-08.16)
    // =================================================================================

    /**
     * TC_M_08_14: Validazione Provincia Troppo Corta
     * Input: provincia = "S"
     */
    @Test
    @DisplayName("TC_M_08_14: Provincia troppo corta")
    void testProvinciaTroppoCorta_TC_M_08_14() {
        logger.info(">>> Esecuzione TC_M_08_14: Validazione Provincia corta");
        ResidenzaEntity input = ResidenzaEntity.builder()
                .viaPiazza("Via Roma").civico("10").comune("Napoli").cap("80100")
                .provincia("S")
                .regione("Campania").paese("Italia")
                .build();
        Exception ex = assertThrows(Exception.class, () ->
                service.creaNucleoFamiliare("CF123", input, false, 0)
        );

        verify(residenzaRepository, never()).save(any());
        logger.info("<<< TC_M_08_14 COMPLETATO");
    }

    /**
     * TC_M_08_15: Validazione Provincia Troppo Lunga
     * Input: provincia = > 20 caratteri
     */
    @Test
    @DisplayName("TC_M_08_15: Provincia troppo lunga")
    void testProvinciaTroppoLunga_TC_M_08_15() {
        logger.info(">>> Esecuzione TC_M_08_15: Validazione Provincia lunga");
        String provinciaLunga = "N".repeat(21);
        ResidenzaEntity input = ResidenzaEntity.builder()
                .viaPiazza("Via Roma").civico("10").comune("Napoli").cap("80100")
                .provincia(provinciaLunga)
                .regione("Campania").paese("Italia")
                .build();
        Exception ex = assertThrows(Exception.class, () ->
                service.creaNucleoFamiliare("CF123", input, false, 0)
        );

        verify(residenzaRepository, never()).save(any());
        logger.info("<<< TC_M_08_15 COMPLETATO");
    }

    /**
     * TC_M_08_16: Validazione Provincia Formato Errato (Numeri)
     * Input: provincia = "Napoli1"
     */
    @Test
    @DisplayName("TC_M_08_16: Provincia con numeri")
    void testProvinciaConNumeri_TC_M_08_16() {
        logger.info(">>> Esecuzione TC_M_08_16: Validazione Provincia numeri");
        ResidenzaEntity input = ResidenzaEntity.builder()
                .viaPiazza("Via Roma").civico("10").comune("Napoli").cap("80100")
                .provincia("Napoli1")
                .regione("Campania").paese("Italia")
                .build();
        Exception ex = assertThrows(Exception.class, () ->
                service.creaNucleoFamiliare("CF123", input, false, 0)
        );
        verify(residenzaRepository, never()).save(any());
        logger.info("<<< TC_M_08_16 COMPLETATO");
    }
}
