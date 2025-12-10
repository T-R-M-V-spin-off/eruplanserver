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
class InviteUserInvalidLengthTest {

    @Mock
    private it.unisa.eruplanserver.IS.Service.GNF.GNFServiceImpl gnfService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private GNFControl controller;

    @Test
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
}