package it.unisa.eruplanserver.IS.Service.GNF;

import it.unisa.eruplanserver.IS.Control.GNF.GNFControl;
import it.unisa.eruplanserver.IS.Entity.GNF.AppoggioEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.NucleoFamiliareEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.ResidenzaEntity;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Exception.GNF.ValidationException;
import it.unisa.eruplanserver.IS.Repository.GNF.AppoggioRepository;
import it.unisa.eruplanserver.IS.Repository.GNF.NucleoFamiliareRepository;
import it.unisa.eruplanserver.IS.Repository.GNF.ResidenzaRepository;
import it.unisa.eruplanserver.IS.Repository.GNF.RichiestaAccessoRepository;
import it.unisa.eruplanserver.IS.Repository.GNF.MembroRepository;
import it.unisa.eruplanserver.IS.Repository.GUM.URRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test integrato per GNFServiceImpl (unit tests).
 * Contiene i test per AppoggioEntity, ResidenzaEntity e rimozione appoggio.
 */
@ExtendWith(MockitoExtension.class)
public class GNFServiceImplTest {

    @InjectMocks
    private GNFServiceImpl gnfService;

    // (opzionale) controller se vuoi testare call indirette in futuro
    @InjectMocks
    private GNFControl controller;

    @Mock
    private URRepository urRepository;

    @Mock
    private AppoggioRepository appoggioRepository;

    @Mock
    private NucleoFamiliareRepository nucleoRepository;

    @Mock
    private ResidenzaRepository residenzaRepository;

    @Mock
    private RichiestaAccessoRepository richiestaRepository;

    @Mock
    private MembroRepository membroRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    // Mocks utili nel setup generico
    @Mock
    private UREntity adminMock;

    @Mock
    private NucleoFamiliareEntity nucleoMock;

    private UREntity mockUtente;

    private AppoggioEntity appoggioStandard;
    private final String CF_ADMIN = "RSSMRA85T10A509Q";

    @BeforeEach
    void setup() {
        // oggetto UREntity "reale" usato in alcuni test
        mockUtente = new UREntity();
        mockUtente.setId(1L);
        mockUtente.setCodiceFiscale("RSSMRA80A01H501U");
        mockUtente.setNucleoFamiliare(null);

        // lenient stubbing per i test che si affidano al setup base (evita fail se non usato)
        lenient().when(urRepository.findByCodiceFiscale(CF_ADMIN)).thenReturn(adminMock);
        lenient().when(adminMock.getNucleoFamiliare()).thenReturn(nucleoMock);

        // appoggio standard valido
        appoggioStandard = AppoggioEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("675")
                .comune("Pompei")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno")
                .build();
    }

    /* ===========================
       Test AppoggioEntity (Validazione Provincia / Regione / Paese)
       =========================== */

    @Test
    @DisplayName("TC-M-09.11: Aggiunta Appoggio fallisce per Provincia troppo corta (<4)")
    void testAggiungiAppoggio_ProvinciaTroppoCorta() {
        // Arrange
        appoggioStandard.setProvincia("Na"); // 2 caratteri

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            gnfService.aggiungiAppoggio(CF_ADMIN, appoggioStandard);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("provincia")
                && exception.getMessage().toLowerCase().contains("troppo"),
                "Messaggio atteso contenente riferimento a 'Provincia' e 'troppo', ottenuto: " + exception.getMessage());
    }

    @Test
    @DisplayName("TC-M-09.12: Aggiunta Appoggio fallisce per Provincia troppo lunga (>20)")
    void testAggiungiAppoggio_ProvinciaTroppoLunga() {
        // Arrange
        appoggioStandard.setProvincia("ccccccccccccccccccccc"); // 21 caratteri

        Exception exception = assertThrows(Exception.class, () -> {
            gnfService.aggiungiAppoggio(CF_ADMIN, appoggioStandard);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("provincia")
                && exception.getMessage().toLowerCase().contains("troppo"),
                "Messaggio atteso contenente riferimento a 'Provincia' e 'troppo', ottenuto: " + exception.getMessage());
    }

    @Test
    @DisplayName("TC-M-09.13: Aggiunta Appoggio fallisce per Provincia con caratteri non validi")
    void testAggiungiAppoggio_ProvinciaCaratteriInvalidi() {
        // Arrange
        appoggioStandard.setProvincia("Napoli23"); // Contiene numeri

        Exception exception = assertThrows(Exception.class, () -> {
            gnfService.aggiungiAppoggio(CF_ADMIN, appoggioStandard);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("provincia")
                        && (exception.getMessage().toLowerCase().contains("caratter") ||
                            exception.getMessage().toLowerCase().contains("non valid")),
                "Messaggio atteso: riferimento a 'Provincia' e 'caratteri non validi', ottenuto: " + exception.getMessage());
    }

    @Test
    @DisplayName("TC-M-09.14: Aggiunta Appoggio fallisce per Regione troppo corta (<5)")
    void testAggiungiAppoggio_RegioneTroppoCorta() {
        // Arrange
        appoggioStandard.setRegione("Camp"); // 4 caratteri

        Exception exception = assertThrows(Exception.class, () -> {
            gnfService.aggiungiAppoggio(CF_ADMIN, appoggioStandard);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("regione")
                && exception.getMessage().toLowerCase().contains("troppo"),
                "Messaggio atteso contenente riferimento a 'Regione' e 'troppo', ottenuto: " + exception.getMessage());
    }

    @Test
    @DisplayName("TC-M-09.15: Aggiunta Appoggio fallisce per Regione troppo lunga (>25)")
    void testAggiungiAppoggio_RegioneTroppoLunga() {
        // Arrange
        appoggioStandard.setRegione("dddddddddddddddddddddddddd"); // 26 char

        Exception exception = assertThrows(Exception.class, () -> {
            gnfService.aggiungiAppoggio(CF_ADMIN, appoggioStandard);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("regione")
                && exception.getMessage().toLowerCase().contains("troppo"),
                "Messaggio atteso contenente riferimento a 'Regione' e 'troppo', ottenuto: " + exception.getMessage());
    }

    @Test
    @DisplayName("TC-M-09.16: Aggiunta Appoggio fallisce per Regione con caratteri non validi")
    void testAggiungiAppoggio_RegioneCaratteriInvalidi() {
        // Arrange
        appoggioStandard.setRegione("Campania2"); // contiene numeri

        Exception exception = assertThrows(Exception.class, () -> {
            gnfService.aggiungiAppoggio(CF_ADMIN, appoggioStandard);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("regione")
                        && (exception.getMessage().toLowerCase().contains("caratter") ||
                            exception.getMessage().toLowerCase().contains("non valid")),
                "Messaggio atteso: riferimento a 'Regione' e 'caratteri non validi', ottenuto: " + exception.getMessage());
    }

    @Test
    @DisplayName("TC-M-09.17: Aggiunta Appoggio fallisce per Paese troppo corto (<4)")
    void testAggiungiAppoggio_PaeseTroppoCorto() {
        // Arrange
        appoggioStandard.setPaese("Mes"); // 3 char

        Exception exception = assertThrows(Exception.class, () -> {
            gnfService.aggiungiAppoggio(CF_ADMIN, appoggioStandard);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("paese")
                && exception.getMessage().toLowerCase().contains("troppo"),
                "Messaggio atteso contenente riferimento a 'Paese' e 'troppo', ottenuto: " + exception.getMessage());
    }

    /* ===========================
       Test di successo per aggiungiAppoggio
       =========================== */

    @Test
    @DisplayName("RF-GNF.09: Aggiunta Appoggio con successo")
    void testAggiungiAppoggio_Successo() throws Exception {
        // Arrange - appoggioStandard considerato valido dal validator
        // Simula che admin abbia nucleo
        when(adminMock.getNucleoFamiliare()).thenReturn(nucleoMock);
        when(urRepository.findByCodiceFiscale(CF_ADMIN)).thenReturn(adminMock);

        // Simula save che ritorna l'entità
        when(appoggioRepository.save(any(AppoggioEntity.class))).thenAnswer(inv -> {
            AppoggioEntity saved = inv.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        // Act
        gnfService.aggiungiAppoggio(CF_ADMIN, appoggioStandard);

        // Assert
        verify(appoggioRepository, times(1)).save(appoggioStandard);
        // appoggioStandard dovrebbe avere il nucleo impostato
        assertEquals(nucleoMock, appoggioStandard.getNucleoFamiliare());
    }

    /* ===========================
       Test per rimozione appoggio (delete)
       =========================== */

    @Test
    @DisplayName("Elimina Appoggio - success quando id esiste e admin appartiene al nucleo")
    void eliminaAppoggio_success_quando_id_esiste_e_admin_appartiene_al_nucleo() throws Exception {
        String cfAdmin = "CFADMIN";
        Long idAppoggio = 2222L;

        NucleoFamiliareEntity nucleo = NucleoFamiliareEntity.builder().id(1L).build();

        UREntity admin = UREntity.builder()
                .codiceFiscale(cfAdmin)
                .nucleoFamiliare(nucleo)
                .build();

        AppoggioEntity appoggio = AppoggioEntity.builder()
                .id(idAppoggio)
                .nucleoFamiliare(nucleo)
                .build();

        when(urRepository.findByCodiceFiscale(cfAdmin)).thenReturn(admin);
        when(appoggioRepository.findById(idAppoggio)).thenReturn(Optional.of(appoggio));

        gnfService.rimuoviAppoggio(cfAdmin, idAppoggio);

        verify(appoggioRepository, times(1)).delete(appoggio);
    }

    /* ===========================
       Test ResidenzaEntity (creaNucleoFamiliare validations)
       =========================== */

    @Test
    @DisplayName("creaNucleoFamiliare - viaPiazza troppo corto")
    void testViaPiazzaTroppoCorto() throws Exception {
        ResidenzaEntity res = ResidenzaEntity.builder()
                .viaPiazza("")
                .civico("675")
                .comune("Pompei")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno")
                .build();

        when(urRepository.findByCodiceFiscale("RSSMRA80A01H501U")).thenReturn(mockUtente);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> gnfService.creaNucleoFamiliare("RSSMRA80A01H501U", res, false, null)
        );

        assertEquals("Nome via/piazza troppo corto", ex.getMessage());
    }

    @Test
    @DisplayName("creaNucleoFamiliare - viaPiazza troppo lungo")
    void testViaPiazzaTroppoLungo() throws Exception {
        ResidenzaEntity res = ResidenzaEntity.builder()
                .viaPiazza("a".repeat(41))
                .civico("675")
                .comune("Pompei")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno")
                .build();

        when(urRepository.findByCodiceFiscale("RSSMRA80A01H501U")).thenReturn(mockUtente);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> gnfService.creaNucleoFamiliare("RSSMRA80A01H501U", res, false, null)
        );

        assertEquals("Nome via/piazza troppo lungo", ex.getMessage());
    }

    @Test
    @DisplayName("creaNucleoFamiliare - viaPiazza caratteri non validi")
    void testViaPiazzaCaratteriNonValidi() throws Exception {
        ResidenzaEntity res = ResidenzaEntity.builder()
                .viaPiazza("Via Sarti%")
                .civico("675")
                .comune("Pompei")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno")
                .build();

        when(urRepository.findByCodiceFiscale("RSSMRA80A01H501U")).thenReturn(mockUtente);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> gnfService.creaNucleoFamiliare("RSSMRA80A01H501U", res, false, null)
        );

        assertEquals("Nome via/piazza contiene caratteri non validi", ex.getMessage());
    }

    /* ===========================
       Test Paese (creaNucleoFamiliare)
       =========================== */

    @Test
    @DisplayName("creaNucleoFamiliare - Paese troppo corto")
    void testPaeseTroppoCorto() throws Exception {
        ResidenzaEntity res = ResidenzaEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("675")
                .comune("Pompei")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Me") // troppo corto
                .build();

        when(urRepository.findByCodiceFiscale("RSSMRA80A01H501U"))
                .thenReturn(mockUtente);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> gnfService.creaNucleoFamiliare(
                        "RSSMRA80A01H501U", res, false, null)
        );

        assertEquals(
                "La creazione del nucleo familiare  non viene effettuata dato che il campo “Paese” è troppo corto.",
                ex.getMessage()
        );
    }

    @Test
    @DisplayName("creaNucleoFamiliare - Paese troppo lungo")
    void testPaeseTroppoLungo() throws Exception {
        String overlyLong = "e".repeat(50);

        ResidenzaEntity res = ResidenzaEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("675")
                .comune("Pompei")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese(overlyLong)
                .build();

        when(urRepository.findByCodiceFiscale("RSSMRA80A01H501U"))
                .thenReturn(mockUtente);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> gnfService.creaNucleoFamiliare(
                        "RSSMRA80A01H501U", res, false, null)
        );

        assertEquals(
                "La creazione del nucleo familiare  non viene effettuata dato che il campo “Paese” è troppo lungo.",
                ex.getMessage()
        );
    }

    @Test
    @DisplayName("creaNucleoFamiliare - Paese contiene caratteri non validi")
    void testPaeseCaratteriNonValidi() throws Exception {
        ResidenzaEntity res = ResidenzaEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("675")
                .comune("Pompei")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno45") // contiene numeri
                .build();

        when(urRepository.findByCodiceFiscale("RSSMRA80A01H501U"))
                .thenReturn(mockUtente);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> gnfService.creaNucleoFamiliare(
                        "RSSMRA80A01H501U", res, false, null)
        );

        assertEquals(
                "La creazione del nucleo familiare  non viene effettuata dato che il campo “Paese” contiene caratteri non validi.",
                ex.getMessage()
        );
    }

    @Test
    @DisplayName("creaNucleoFamiliare - Paese valido (success)")
    void testPaeseValido() throws Exception {
        ResidenzaEntity res = ResidenzaEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("675")
                .comune("Pompei")
                .cap("67489")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno")
                .build();

        when(urRepository.findByCodiceFiscale("RSSMRA80A01H501U"))
                .thenReturn(mockUtente);

        when(residenzaRepository.save(any(ResidenzaEntity.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(nucleoRepository.save(any(NucleoFamiliareEntity.class)))
                .thenAnswer(i -> {
                    NucleoFamiliareEntity nucleo = i.getArgument(0);
                    nucleo.setId(10L);
                    return nucleo;
                });

        NucleoFamiliareEntity created =
                gnfService.creaNucleoFamiliare(
                        "RSSMRA80A01H501U", res, false, null
                );

        assertNotNull(created);
        assertEquals(10L, created.getId());
        assertEquals("Messigno", created.getResidenza().getPaese());
    }
}
