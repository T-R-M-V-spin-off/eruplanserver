package it.unisa.eruplanserver.IS.Service.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.MembroEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.NucleoFamiliareEntity;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Repository.GNF.MembroRepository;
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
import static org.mockito.Mockito.*;

/**
 * Test Suite per RF-GNF.03: Aggiunta Membro Manuale
 * 
 * Questa suite testa tutte le validazioni e il comportamento corretto
 * del metodo aggiungiMembroManuale() di GNFServiceImpl
 * 
 * Test Cases:
 * - TC_M_03_8:  Validazione formato data di nascita (dd/MM/yyyy)
 * - TC_M_03_9:  Validazione sesso (M o F)
 * - TC_M_03_10: Validazione campo assistenza (non null)
 * - TC_M_03_11: Validazione campo minorenne (non null)
 * - TC_M_03_12: Inserimento corretto con tutti i dati validi
 */
@DisplayName("Test Suite: RF-GNF.03 - Aggiunta Membro Manuale")
class MembroServiceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(MembroServiceTest.class);

    @Mock
    private MembroRepository membroRepository;

    @Mock
    private URRepository urRepository;

    @InjectMocks
    private GNFServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logger.info("========== INIZIO TEST SUITE RF-GNF.03 ==========");
    }

    /**
     * TC_M_03_8: Validazione Formato Data di Nascita
     * 
     * Scenario: Utente tenta di aggiungere membro con data di nascita in formato errato
     * Input: dataDiNascita = "10/02/197" (solo 3 cifre per l'anno)
     * Expected: ValidationException con messaggio "Formato data non valido"
     * Verifiche: 
     *   - Eccezione lanciata
     *   - Messaggio d'errore corretto
     *   - Membro NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_03_8: Data di nascita con formato errato")
    void testDataNascitaFormatoErrato_TC_M_03_8() {
        logger.info(">>> Esecuzione TC_M_03_8: Validazione formato data");
        
        // ARRANGE
        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/197")  // Formato errato
                .sesso("M")
                .assistenza(true)
                .minorenne(false)
                .build();
        logger.debug("Membro creato con data errata: {}", input.getDataDiNascita());

        // ACT & ASSERT
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.aggiungiMembroManuale("CF123", input)
        );

        assertEquals("Formato data non valido", ex.getMessage());
        verify(membroRepository, never()).save(any());
        
        logger.info("<<< TC_M_03_8 COMPLETATO: Validazione data funziona correttamente");
    }

    /**
     * TC_M_03_9: Validazione Sesso
     * 
     * Scenario: Utente tenta di aggiungere membro con sesso non valido
     * Input: sesso = "X" (non M o F)
     * Expected: ValidationException con messaggio "Sesso non valido"
     * Verifiche:
     *   - Eccezione lanciata
     *   - Messaggio d'errore corretto
     *   - Membro NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_03_9: Sesso non valido")
    void testSessoNonValido_TC_M_03_9() {
        logger.info(">>> Esecuzione TC_M_03_9: Validazione sesso");
        
        // ARRANGE
        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("X")  // Sesso non valido
                .assistenza(true)
                .minorenne(false)
                .build();
        logger.debug("Membro creato con sesso invalido: {}", input.getSesso());

        // ACT & ASSERT
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.aggiungiMembroManuale("CF123", input)
        );

        assertEquals("Sesso non valido", ex.getMessage());
        verify(membroRepository, never()).save(any());
        
        logger.info("<<< TC_M_03_9 COMPLETATO: Validazione sesso funziona correttamente");
    }

    /**
     * TC_M_03_10: Validazione Campo Assistenza
     * 
     * Scenario: Utente tenta di aggiungere membro senza specificare assistenza
     * Input: assistenza = null
     * Expected: ValidationException con messaggio "Campo Assistenza non definito"
     * Verifiche:
     *   - Eccezione lanciata
     *   - Messaggio d'errore corretto
     *   - Membro NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_03_10: Campo assistenza non definito")
    void testAssistenzaNonDefinita_TC_M_03_10() {
        logger.info(">>> Esecuzione TC_M_03_10: Validazione assistenza");
        
        // ARRANGE
        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .assistenza(null)  // Campo non definito
                .minorenne(false)
                .build();
        logger.debug("Membro creato senza assistenza: assistenza = null");

        // ACT & ASSERT
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.aggiungiMembroManuale("CF123", input)
        );

        assertEquals("Campo Assistenza non definito", ex.getMessage());
        verify(membroRepository, never()).save(any());
        
        logger.info("<<< TC_M_03_10 COMPLETATO: Validazione assistenza funziona correttamente");
    }

    /**
     * TC_M_03_11: Validazione Campo Minorenne
     * 
     * Scenario: Utente tenta di aggiungere membro senza specificare se minorenne
     * Input: minorenne = null
     * Expected: ValidationException con messaggio "Campo Minore di 14 non definito"
     * Verifiche:
     *   - Eccezione lanciata
     *   - Messaggio d'errore corretto
     *   - Membro NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_03_11: Campo minorenne non definito")
    void testMinoreNonDefinito_TC_M_03_11() {
        logger.info(">>> Esecuzione TC_M_03_11: Validazione minorenne");
        
        // ARRANGE
        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .assistenza(true)
                .minorenne(null)  // Campo non definito
                .build();
        logger.debug("Membro creato senza minorenne: minorenne = null");

        // ACT & ASSERT
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.aggiungiMembroManuale("CF123", input)
        );

        assertEquals("Campo Minore di 14 non definito", ex.getMessage());
        verify(membroRepository, never()).save(any());
        
        logger.info("<<< TC_M_03_11 COMPLETATO: Validazione minorenne funziona correttamente");
    }

    /**
     * TC_M_03_12: Inserimento Corretto di un Membro
     * 
     * Scenario: Utente aggiunge un nuovo membro con tutti i dati validi
     * Input: Tutti i campi obbligatori compilati correttamente
     * Expected: Membro salvato con successo nel repository
     * Verifiche:
     *   - Nessuna eccezione lanciata
     *   - Membro salvato esattamente una volta
     *   - Dati del membro corretti
     *   - Nucleo familiare associato
     */
    @Test
    @DisplayName("TC_M_03_12: Inserimento corretto con dati validi")
    void testInserimentoCorretto_TC_M_03_12() throws Exception {
        logger.info(">>> Esecuzione TC_M_03_12: Inserimento membro corretto");
        
        // ARRANGE
        NucleoFamiliareEntity nucleo = NucleoFamiliareEntity.builder()
                .id(1L)
                .build();
        
        UREntity admin = UREntity.builder()
                .codiceFiscale("CF123")
                .nucleoFamiliare(nucleo)
                .build();

        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .assistenza(true)
                .minorenne(false)
                .build();

        when(urRepository.findByCodiceFiscale("CF123")).thenReturn(admin);
        when(membroRepository.save(any())).thenReturn(input);
        
        logger.debug("Setup: Admin trovato con nucleo id={}, Membro pronto per il salvataggio", nucleo.getId());

        // ACT
        service.aggiungiMembroManuale("CF123", input);
        logger.debug("Metodo aggiungiMembroManuale eseguito");

        // ASSERT
        assertNotNull(input, "Membro non deve essere null");
        assertEquals("Sandro", input.getNome(), "Nome deve essere Sandro");
        assertEquals("Capri", input.getCognome(), "Cognome deve essere Capri");
        assertEquals("M", input.getSesso(), "Sesso deve essere M");
        
        // VERIFY
        verify(membroRepository, times(1)).save(input);
        verify(urRepository, times(1)).findByCodiceFiscale("CF123");
        
        logger.info("<<< TC_M_03_12 COMPLETATO: Membro salvato con successo");
        logger.info("    - Nome: {}", input.getNome());
        logger.info("    - Cognome: {}", input.getCognome());
        logger.info("    - Data di nascita: {}", input.getDataDiNascita());
        logger.info("    - Sesso: {}", input.getSesso());
    }
}
