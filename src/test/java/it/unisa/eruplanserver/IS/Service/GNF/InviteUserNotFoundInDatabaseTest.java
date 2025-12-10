package it.unisa.eruplanserver.IS.Control.GNF;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteUserNotFoundInDatabaseTest {

    @Mock
    private it.unisa.eruplanserver.IS.Service.GNF.GNFServiceImpl gnfService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private GNFControl controller;

    @Test
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
}