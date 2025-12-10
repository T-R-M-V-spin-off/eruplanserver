package it.unisa.eruplanserver.IS.Service.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.AppoggioEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.NucleoFamiliareEntity;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Repository.GNF.AppoggioRepository;
import it.unisa.eruplanserver.IS.Repository.GUM.URRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GNFServiceImplTest {

    @InjectMocks
    private GNFServiceImpl gnfService;

    @Mock
    private URRepository urRepository;

    @Mock
    private AppoggioRepository appoggioRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRemoveAppoggioSuccess() throws Exception {
        // Arrange
        String cfAdmin = "CF_ADMIN_ABC";
        Long idAppoggio = 2222L;

        UREntity admin = new UREntity();
        admin.setId(10L);

        NucleoFamiliareEntity nucleo = new NucleoFamiliareEntity();
        nucleo.setId(5L);
        admin.setNucleoFamiliare(nucleo);

        AppoggioEntity appoggio = new AppoggioEntity();
        appoggio.setId(idAppoggio);
        appoggio.setNucleoFamiliare(nucleo);

        when(urRepository.findByCodiceFiscale(cfAdmin)).thenReturn(admin);
        when(appoggioRepository.findById(idAppoggio)).thenReturn(Optional.of(appoggio));

        // Act & Assert
        assertDoesNotThrow(() -> gnfService.rimuoviAppoggio(cfAdmin, idAppoggio));

        // Verify repository interactions
        verify(appoggioRepository, times(1)).delete(appoggio);
        verify(urRepository, times(1)).findByCodiceFiscale(cfAdmin);
        verify(appoggioRepository, times(1)).findById(idAppoggio);
    }
}