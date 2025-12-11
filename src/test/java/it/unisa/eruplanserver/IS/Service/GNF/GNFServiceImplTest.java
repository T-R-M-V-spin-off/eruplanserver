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
import it.unisa.eruplanserver.IS.Repository.GUM.URRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GNFServiceIntegratedTest {

    @InjectMocks
    private GNFServiceImpl gnfService;

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
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    private UREntity mockUtente;

    @BeforeEach
    void setup() {
        mockUtente = new UREntity();
        mockUtente.setId(1L);
        mockUtente.setCodiceFiscale("RSSMRA80A01H501U");
        mockUtente.setNucleoFamiliare(null);
    }

    /* ===========================
       Test AppoggioEntity
       =========================== */
    @Test
    @SneakyThrows
    void testAggiuntaAppoggioTC_M_9_18() {
        AppoggioEntity appoggio = AppoggioEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("675")
                .cap("67489")
                .comune("Pompei")
                .provincia("Napoli")
                .regione("Campania")
                .paese("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee") // 41 char
                .build();

        NucleoFamiliareEntity nucleo = NucleoFamiliareEntity.builder().id(1L).build();

        UREntity admin = UREntity.builder()
                .codiceFiscale("RSSMRA85M01H501U")
                .nucleoFamiliare(nucleo)
                .build();

        when(urRepository.findByCodiceFiscale(admin.getCodiceFiscale())).thenReturn(admin);

        assertThrows(Exception.class, () -> gnfService.aggiungiAppoggio(admin.getCodiceFiscale(), appoggio));
    }

    @Test
    @SneakyThrows
    void testAggiuntaAppoggioTC_M_9_19() {
        AppoggioEntity appoggio = AppoggioEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("675")
                .cap("67489")
                .comune("Pompei")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno45")
                .build();

        NucleoFamiliareEntity nucleo = NucleoFamiliareEntity.builder().id(1L).build();

        UREntity admin = UREntity.builder()
                .codiceFiscale("RSSMRA85M01H501U")
                .nucleoFamiliare(nucleo)
                .build();

        when(urRepository.findByCodiceFiscale(admin.getCodiceFiscale())).thenReturn(admin);

        assertThrows(Exception.class, () -> gnfService.aggiungiAppoggio(admin.getCodiceFiscale(), appoggio));
    }

    @Test
    @SneakyThrows
    void testAggiuntaAppoggioTC_M_9_20() {
        AppoggioEntity appoggio = AppoggioEntity.builder()
                .viaPiazza("Via Sarti")
                .civico("675")
                .cap("67489")
                .comune("Pompei")
                .provincia("Napoli")
                .regione("Campania")
                .paese("Messigno")
                .build();

        NucleoFamiliareEntity nucleo = NucleoFamiliareEntity.builder().id(1L).build();

        when(appoggioRepository.save(any(AppoggioEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UREntity admin = UREntity.builder()
                .codiceFiscale("RSSMRA85M01H501U")
                .nucleoFamiliare(nucleo)
                .build();

        when(urRepository.findByCodiceFiscale(admin.getCodiceFiscale())).thenReturn(admin);

        gnfService.aggiungiAppoggio(admin.getCodiceFiscale(), appoggio);
        verify(appoggioRepository, times(1)).save(any(AppoggioEntity.class));
    }

    @Test
    @SneakyThrows
    void eliminaAppoggio_success_quando_id_esiste_e_admin_appartiene_al_nucleo() {
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
       Test ResidenzaEntity
       =========================== */
    @Test
    void testPaeseTroppoCorto() {
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
    void testPaeseTroppoLungo() {
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
    void testPaeseCaratteriNonValidi() {
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

    /* ===========================
       Test InvitaUtente nel controller
       =========================== */
    @Test
    @SneakyThrows
    void whenCodiceFiscaleHasInvalidLength_thenBadRequestReturned() {
        String cfAdmin = "CFADMINEXAMPLEAA";
        String cfInvitato = "GRT36TGHH53"; // length != 16

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("codiceFiscale")).thenReturn(cfAdmin);

        ResponseEntity<String> response = controller.invitaUtente(cfInvitato, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
                "L'invito di un utente nel proprio nucleo familiare non viene effettuato dato che il campo \"CodiceFiscale\" non è composto da 16 caratteri.",
                response.getBody()
        );

        verifyNoInteractions(gnfService);
    }

    @Test
    @SneakyThrows
    void whenCodiceFiscaleContainsInvalidCharacters_thenBadRequestReturned() {
        String cfAdmin = "CFADMINEXAMPLEAA";
        String cfInvitato = "GRT36T$%GHH5334G"; // 16 chars invalid

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("codiceFiscale")).thenReturn(cfAdmin);

        ResponseEntity<String> response = controller.invitaUtente(cfInvitato, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
                "L'invito di un utente nel proprio nucleo familiare non viene effettuato dato che il campo \"CodiceFiscale\" contiene caratteri non validi.",
                response.getBody()
        );

        verifyNoInteractions(gnfService);
    }

    @Test
    @SneakyThrows
    void whenCodiceFiscaleValidButNotInDb_thenBadRequestWithServiceMessage() {
        String cfAdmin = "CFADMINEXAMPLEAA";
        String cfInvitato = "GRT36T56GHH5334G"; // 16 chars valid

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("codiceFiscale")).thenReturn(cfAdmin);

        String expectedServiceMessage = "L'invito di un utente nel proprio nucleo familiare non viene effettuato dato che il campo \"CodiceFiscale\" non ha nessuna corrispondenza con un utente sul DB.";
        doThrow(new Exception(expectedServiceMessage)).when(gnfService).invitaUtente(cfAdmin, cfInvitato);

        ResponseEntity<String> response = controller.invitaUtente(cfInvitato, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedServiceMessage, response.getBody());

        verify(gnfService, times(1)).invitaUtente(cfAdmin, cfInvitato);
    }

    @Test
    @SneakyThrows
    void whenCodiceFiscaleValidAndExists_thenReturnOk() {
        String cfAdmin = "CFADMINEXAMPLEAA";
        String cfInvitato = "HTL34DEF7HFHJ77G"; // valid 16 chars

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("codiceFiscale")).thenReturn(cfAdmin);

        doNothing().when(gnfService).invitaUtente(cfAdmin, cfInvitato);

        ResponseEntity<String> response = controller.invitaUtente(cfInvitato, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Invito inviato con successo.", response.getBody());

        verify(gnfService, times(1)).invitaUtente(cfAdmin, cfInvitato);
    }
}
