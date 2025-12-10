package it.unisa.eruplanserver.IS.Control.GNF;

import it.unisa.eruplanserver.IS.Service.GNF.GNFService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.mock.web.MockHttpSession;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GNFControlTest {

    private MockMvc mockMvc;
    private GNFControl controller;
    private GNFService gnfServiceMock;

    @BeforeEach
    void setup() {
        controller = new GNFControl();
        gnfServiceMock = Mockito.mock(GNFService.class);
        ReflectionTestUtils.setField(controller, "gnfService", gnfServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testDeleteAppoggioSuccess() throws Exception {
        // Arrange
        Long id = 2222L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("codiceFiscale", "CF_ADMIN_ABC");

        doNothing().when(gnfServiceMock).rimuoviAppoggio("CF_ADMIN_ABC", id);

        // Act & Assert
        mockMvc.perform(delete("/gestoreNucleo/appoggi/rimuovi/{id}", id).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("Appoggio rimosso con successo."));

        // Verify interactions
        verify(gnfServiceMock, times(1)).rimuoviAppoggio("CF_ADMIN_ABC", id);
        verifyNoMoreInteractions(gnfServiceMock);
    }
}