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
class InviteUserSuccessTest {

    @Mock
    private it.unisa.eruplanserver.IS.Service.GNF.GNFServiceImpl gnfService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private GNFControl controller;

    @Test
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