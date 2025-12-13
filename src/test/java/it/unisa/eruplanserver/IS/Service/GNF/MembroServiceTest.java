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
 * - TC_M_03_1:  Lunghezza nome troppo corta
 * - TC_M_03_2:  Lunghezza nome troppo elevata
 * - TC_M_03_3:  Nome con caratteri non accettati
 * - TC_M_03_4:  Lunghezza cognome troppo corta
 * - TC_M_03_5:  Lunghezza cognome troppo elevata
 * - TC_M_03_6:  Cognome con caratteri non accettati
 * - TC_M_03_7:  Codice Fiscale non valido
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

    // Costante per simulare il CF dell'admin in tutti i test
    private final String CF_ADMIN = "CF123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logger.info("========== INIZIO TEST SUITE RF-GNF.03 ==========");
    }

    /**
     * TC_M_03_1: Lunghezza nome troppo corta
     * * Scenario: Utente tenta di aggiungere membro con nome troppo corto
     * Input: nome = "a" (lunghezza 1)
     * Expected: ValidationException con messaggio specifico
     * Verifiche:
     * - Eccezione lanciata
     * - Messaggio d'errore corretto ("Nome troppo corto")
     * - Membro NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_03_1: Lunghezza nome troppo corta")
    void testNomeTroppoCorto_TC_M_03_1() {
        logger.info(">>> Esecuzione TC_M_03_1: Validazione nome corto");

        // --- ARRANGE ---
        // Creazione dell'oggetto MembroEntity con dati validi, eccetto il Nome
        MembroEntity input = MembroEntity.builder()
                .nome("a")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .assistenza(true)
                .minorenne(false)
                .build();
        logger.debug("Membro creato con nome corto: {}", input.getNome());

        // --- ACT & ASSERT ---
        // Tentiamo di eseguire il metodo. Ci aspettiamo che il validatore blocchi l'esecuzione
        // lanciando una ValidationException prima di arrivare alla logica di business.
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.aggiungiMembroManuale(CF_ADMIN, input)
        );

        // --- VERIFY ---
        // 1. Verifichiamo che il messaggio dell'eccezione sia esattamente quello atteso
        assertEquals("Nome troppo corto", ex.getMessage());

        // 2. Verifichiamo che il repository non sia MAI stato chiamato per salvare
        verify(membroRepository, never()).save(any());

        logger.info("<<< TC_M_03_1 COMPLETATO");
    }

    /**
     * TC_M_03_2: Lunghezza nome troppo elevata
     * * Scenario: Utente tenta di aggiungere membro con nome troppo lungo
     * Input: nome = "A...a" (lunghezza 31)
     * Expected: ValidationException con messaggio specifico
     * Verifiche:
     * - Eccezione lanciata
     * - Messaggio d'errore corretto ("Nome troppo lungo")
     * - Membro NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_03_2: Lunghezza nome troppo elevata")
    void testNomeTroppoLungo_TC_M_03_2() {
        logger.info(">>> Esecuzione TC_M_03_2: Validazione nome lungo");

        MembroEntity input = MembroEntity.builder()
                .nome("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa") // 31 chars
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .assistenza(true)
                .minorenne(false)
                .build();

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.aggiungiMembroManuale(CF_ADMIN, input)
        );

        assertEquals("Nome troppo lungo", ex.getMessage());
        verify(membroRepository, never()).save(any());
    }

    /**
     * TC_M_03_3: Nome con caratteri non accettati
     * * Scenario: Utente tenta di aggiungere membro con numeri nel nome
     * Input: nome = "Sandro4"
     * Expected: ValidationException con messaggio specifico
     * Verifiche:
     * - Eccezione lanciata
     * - Messaggio d'errore corretto ("Nome non valido")
     * - Membro NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_03_3: Nome con caratteri invalidi")
    void testNomeCaratteriInvalidi_TC_M_03_3() {
        logger.info(">>> Esecuzione TC_M_03_3: Validazione caratteri nome");

        MembroEntity input = MembroEntity.builder()
                .nome("Sandro4")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .assistenza(true)
                .minorenne(false)
                .build();

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.aggiungiMembroManuale(CF_ADMIN, input)
        );

        assertEquals("Nome non valido", ex.getMessage());
        verify(membroRepository, never()).save(any());
    }

    /**
     * TC_M_03_4: Lunghezza cognome troppo corta
     * * Scenario: Utente tenta di aggiungere membro con cognome troppo corto
     * Input: cognome = "C" (lunghezza 1)
     * Expected: ValidationException con messaggio specifico
     * Verifiche:
     * - Eccezione lanciata
     * - Messaggio d'errore corretto ("Cognome troppo corto")
     * - Membro NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_03_4: Lunghezza cognome troppo corta")
    void testCognomeTroppoCorto_TC_M_03_4() {
        logger.info(">>> Esecuzione TC_M_03_4: Validazione cognome corto");

        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("C")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .assistenza(true)
                .minorenne(false)
                .build();

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.aggiungiMembroManuale(CF_ADMIN, input)
        );

        assertEquals("Cognome troppo corto", ex.getMessage());
        verify(membroRepository, never()).save(any());
    }

    /**
     * TC_M_03_5: Lunghezza cognome troppo elevata
     * * Scenario: Utente tenta di aggiungere membro con cognome troppo lungo
     * Input: cognome = "bbbb..." (lunghezza 31)
     * Expected: ValidationException con messaggio specifico
     * Verifiche:
     * - Eccezione lanciata
     * - Messaggio d'errore corretto ("Cognome troppo lungo")
     * - Membro NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_03_5: Lunghezza cognome troppo elevata")
    void testCognomeTroppoLungo_TC_M_03_5() {
        logger.info(">>> Esecuzione TC_M_03_5: Validazione cognome lungo");

        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .assistenza(true)
                .minorenne(false)
                .build();

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.aggiungiMembroManuale(CF_ADMIN, input)
        );

        assertEquals("Cognome troppo lungo", ex.getMessage());
        verify(membroRepository, never()).save(any());
    }

    /**
     * TC_M_03_6: Cognome con caratteri non accettati
     * * Scenario: Utente tenta di aggiungere membro con numeri nel cognome
     * Input: cognome = "Capri3"
     * Expected: ValidationException con messaggio specifico
     * Verifiche:
     * - Eccezione lanciata
     * - Messaggio d'errore corretto ("Cognome non valido")
     * - Membro NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_03_6: Cognome con caratteri invalidi")
    void testCognomeCaratteriInvalidi_TC_M_03_6() {
        logger.info(">>> Esecuzione TC_M_03_6: Validazione caratteri cognome");

        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("Capri3")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .assistenza(true)
                .minorenne(false)
                .build();

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.aggiungiMembroManuale(CF_ADMIN, input)
        );

        assertEquals("Cognome non valido", ex.getMessage());
        verify(membroRepository, never()).save(any());
    }

    /**
     * TC_M_03_7: Codice Fiscale non valido
     * * Scenario: Utente tenta di aggiungere membro con CF corto e caratteri speciali
     * Input: codiceFiscale = "CDT02DGE34$FE4"
     * Expected: ValidationException con messaggio specifico
     * Verifiche:
     * - Eccezione lanciata
     * - Messaggio d'errore corretto ("Codice Fiscale non valido")
     * - Membro NON salvato in repository
     */
    @Test
    @DisplayName("TC_M_03_7: Codice Fiscale invalido")
    void testCodiceFiscaleErrato_TC_M_03_7() {
        logger.info(">>> Esecuzione TC_M_03_7: Validazione Codice Fiscale");

        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34$FE4")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .assistenza(true)
                .minorenne(false)
                .build();

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.aggiungiMembroManuale(CF_ADMIN, input)
        );

        assertEquals("Codice Fiscale non valido", ex.getMessage());
        verify(membroRepository, never()).save(any());
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
