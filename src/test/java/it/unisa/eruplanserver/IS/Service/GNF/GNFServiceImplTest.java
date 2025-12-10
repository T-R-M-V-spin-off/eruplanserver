package it.unisa.eruplanserver.IS.Service.GNF;


import it.unisa.eruplanserver.IS.Control.GNF.GNFControl;
import it.unisa.eruplanserver.IS.Entity.GNF.AppoggioEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.NucleoFamiliareEntity;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Repository.GNF.AppoggioRepository;
import it.unisa.eruplanserver.IS.Repository.GUM.URRepository;
import it.unisa.eruplanserver.IS.Service.GNF.GNFServiceImpl;
import it.unisa.eruplanserver.IS.Utility.Validator;
import lombok.SneakyThrows;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
public class GNFServiceImplTest {
    @InjectMocks
    private GNFServiceImpl gnfService; 

    @Mock
    private URRepository urRepository;

    @Mock
    private AppoggioRepository appoggioRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private GNFControl controller;

    /* ===========================
       Test originale (TC-M-9-18)
       =========================== */
    @Test
    @SneakyThrows
    void testAggiuntaAppoggioTC_M_9_18() {

        AppoggioEntity Appoggio = AppoggioEntity.builder()
                .viaPiazza("Via Roma")
                .civico("10")
                .cap("80100")
                .comune("Napoli")
                .provincia("NA")
                .regione("Campania")
                .paese("Italiaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa") // >40 char
                .build();

        UREntity admin = UREntity.builder()
                .codiceFiscale("RSSMRA85M01H501U")
                .build();

        when(urRepository.findByCodiceFiscale(admin.getCodiceFiscale())).thenReturn(admin);

        assertThrows(Exception.class, () ->gnfService.aggiungiAppoggio(admin.getCodiceFiscale(), Appoggio));
    }

    /* ===========================
       TC-M-10.5: eliminaAppoggio
       =========================== */
    @Test
    @SneakyThrows
    void eliminaAppoggio_success_quando_id_esiste_e_admin_appartiene_al_nucleo(){
        // TC-M-10.5: scenario positivo
        String cfAdmin = "CFADMIN";
        Long idAppoggio = 2222L;

        // costruisco il nucleo con lo stesso id per admin e appoggio (condizione di sicurezza)
        NucleoFamiliareEntity nucleo = NucleoFamiliareEntity.builder().id(1L).build();

        UREntity admin = UREntity.builder()
                .codiceFiscale(cfAdmin)
                .nucleoFamiliare(nucleo)
                .build();

        AppoggioEntity appoggio = AppoggioEntity.builder()
                .id(idAppoggio)
                .nucleoFamiliare(nucleo)
                .build();

        // mock dei repository come nel servizio reale
        when(urRepository.findByCodiceFiscale(cfAdmin)).thenReturn(admin);
        when(appoggioRepository.findById(idAppoggio)).thenReturn(Optional.of(appoggio));

        // eseguo il metodo sotto test — non deve lanciare eccezione
        gnfService.rimuoviAppoggio(cfAdmin, idAppoggio);

        // verifico che l'entità sia stata eliminata (delete chiamato esattamente 1 volta)
        verify(appoggioRepository, times(1)).delete(appoggio);
    }

    /* ===========================
       TC-M-01.1: InviteUserInvalidLengthTest
       =========================== */
    @Test
    @SneakyThrows
    void whenCodiceFiscaleHasInvalidLength_thenBadRequestReturned() {
        // TC: LC != 16
        String cfAdmin = "CFADMINEXAMPLEAA";
        String cfInvitato = "GRT36TGHH53"; // length != 16

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("codiceFiscale")).thenReturn(cfAdmin);

        ResponseEntity<String> response = controller.invitaUtente(cfInvitato, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("L'invito di un utente nel proprio nucleo familiare non viene effettuato dato che il campo \"CodiceFiscale\" non è composto da 16 caratteri.",
                response.getBody());

        // service must NOT be invoked
        verifyNoInteractions(gnfService);
    }

    /* ===========================
       TC-M-01.2: InviteUserInvalidCharactersTest
       =========================== */
    @Test
    @SneakyThrows
    void whenCodiceFiscaleContainsInvalidCharacters_thenBadRequestReturned() {
        // TC: LC = 16 but contains invalid characters
        String cfAdmin = "CFADMINEXAMPLEAA";
        String cfInvitato = "GRT36T$%GHH5334G"; // 16 chars but invalid chars

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("codiceFiscale")).thenReturn(cfAdmin);

        ResponseEntity<String> response = controller.invitaUtente(cfInvitato, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("L'invito di un utente nel proprio nucleo familiare non viene effettuato dato che il campo \"CodiceFiscale\" contiene caratteri non validi.",
                response.getBody());

        // service must NOT be invoked
        verifyNoInteractions(gnfService);
    }

    /* ===========================
       TC-M-01.3: InviteUserNotFoundInDatabaseTest
       =========================== */
    @Test
    @SneakyThrows
    void whenCodiceFiscaleValidButNotInDb_thenBadRequestWithServiceMessage() throws Exception {
        // TC: LC = 16, CC valid, EDB = does not exist
        String cfAdmin = "CFADMINEXAMPLEAA";
        String cfInvitato = "GRT36T56GHH5334G"; // 16 chars valid pattern-wise

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("codiceFiscale")).thenReturn(cfAdmin);

        // Simulate service throwing the "not found" exception with the TD-specific message
        String expectedServiceMessage = "L'invito di un utente nel proprio nucleo familiare non viene effettuato dato che il campo \"CodiceFiscale\" non ha nessuna corrispondenza con un utente sul DB.";
        doThrow(new Exception(expectedServiceMessage)).when(gnfService).invitaUtente(cfAdmin, cfInvitato);

        ResponseEntity<String> response = controller.invitaUtente(cfInvitato, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedServiceMessage, response.getBody());

        // verify service invoked exactly once
        verify(gnfService, times(1)).invitaUtente(cfAdmin, cfInvitato);
    }

    /* ===========================
       TC-M-01.4: InviteUserSuccessTest
       =========================== */
    @Test
    @SneakyThrows
    void whenCodiceFiscaleValidAndExists_thenReturnOk() throws Exception {
        // TC: LC = 16, CC valid, EDB = exists
        String cfAdmin = "CFADMINEXAMPLEAA";
        String cfInvitato = "HTL34DEF7HFHJ77G"; // valid 16 chars

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("codiceFiscale")).thenReturn(cfAdmin);

        // Service does not throw -> success path
        doNothing().when(gnfService).invitaUtente(cfAdmin, cfInvitato);

        ResponseEntity<String> response = controller.invitaUtente(cfInvitato, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Invito inviato con successo.", response.getBody());

        verify(gnfService, times(1)).invitaUtente(cfAdmin, cfInvitato);
    }
}
