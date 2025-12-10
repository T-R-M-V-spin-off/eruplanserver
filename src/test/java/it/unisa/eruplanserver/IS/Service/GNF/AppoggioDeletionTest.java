package it.unisa.eruplanserver.IS.Service.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.AppoggioEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.NucleoFamiliareEntity;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Repository.GNF.AppoggioRepository;
import it.unisa.eruplanserver.IS.Repository.GUM.URRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppoggioDeletionTest {

    @Mock
    private URRepository urRepository;

    @Mock
    private AppoggioRepository appoggioRepository;

    @InjectMocks
    private GNFServiceImpl gnfService; // usa l'implementazione reale che hai nel progetto

    @Test
    void eliminaAppoggio_success_quando_id_esiste_e_admin_appartiene_al_nucleo() throws Exception {
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
}
