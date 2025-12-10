package it.unisa.eruplanserver.IS.Service.GNF;


import it.unisa.eruplanserver.IS.Entity.GNF.AppoggioEntity;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Repository.GNF.AppoggioRepository;
import it.unisa.eruplanserver.IS.Repository.GUM.URRepository;
import it.unisa.eruplanserver.IS.Utility.Validator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GNFServiceImplTest {
    @InjectMocks
    private GNFServiceImpl gnfService;
    private Validator validator;
    @Mock
    private URRepository urRepository;

    @Test
    @SneakyThrows
    void testAggiuntaAppoggioTC_M_9_18(){

        AppoggioEntity Appoggio = AppoggioEntity.builder()
                .viaPiazza("Via Roma")
                .civico("10")
                .cap("80100")
                .comune("Napoli")
                .provincia("NA")
                .regione("Campania")
                .paese("Italiaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")//>40 char
                .build();

        UREntity admin = UREntity.builder()
                .codiceFiscale("RSSMRA85M01H501U")
                .build();

        when(urRepository.findByCodiceFiscale(admin.getCodiceFiscale())).thenReturn(admin);

        assertThrows(Exception.class, () -> {gnfService.aggiungiAppoggio(admin.getCodiceFiscale(), Appoggio);
        });
    }

}
