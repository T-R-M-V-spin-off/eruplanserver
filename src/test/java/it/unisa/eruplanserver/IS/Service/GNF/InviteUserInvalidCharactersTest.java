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
class InviteUserInvalidCharactersTest {

    @Mock
    private it.unisa.eruplanserver.IS.Service.GNF.GNFServiceImpl gnfService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private GNFControl controller;

    @Test
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
}