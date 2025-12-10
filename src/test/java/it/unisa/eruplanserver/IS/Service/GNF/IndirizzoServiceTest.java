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

}
