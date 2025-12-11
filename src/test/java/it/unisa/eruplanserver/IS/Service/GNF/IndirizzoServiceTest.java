package it.unisa.eruplanserver.IS.Service.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.ResidenzaEntity;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Repository.GNF.ResidenzaRepository;
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
