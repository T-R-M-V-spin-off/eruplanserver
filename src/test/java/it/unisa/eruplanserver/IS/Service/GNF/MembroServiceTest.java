package it.unisa.eruplanserver.IS.Service.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.MembroEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.NucleoFamiliareEntity;
import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Repository.GNF.MembroRepository;
import it.unisa.eruplanserver.IS.Repository.GUM.URRepository;
import it.unisa.eruplanserver.IS.Exception.GNF.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MembroServiceTest {

    private MembroRepository repositoryMock;
    private URRepository urRepository;
    private GNFServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        repositoryMock = mock(MembroRepository.class);
        urRepository = mock(URRepository.class);

        service = new GNFServiceImpl();

        // Iniettare i repository mock nei campi privati di GNFServiceImpl tramite reflection
        Field f1 = GNFServiceImpl.class.getDeclaredField("membroRepository");
        f1.setAccessible(true);
        f1.set(service, repositoryMock);

        Field f2 = GNFServiceImpl.class.getDeclaredField("urRepository");
        f2.setAccessible(true);
        f2.set(service, urRepository);
    }

    @Test
    void testDataNascitaFormatoErrato_TC_M_03_8() {
        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/197")
                .sesso("M")
                .assistenza(true)
                .minorenne(false)
                .build();

        Exception ex = assertThrows(ValidationException.class, () -> service.aggiungiMembroManuale("CF123", input));
        assertEquals("Formato data non valido", ex.getMessage());
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void testSessoNonValido_TC_M_03_9() {
        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("X")
                .assistenza(true)
                .minorenne(false)
                .build();

        Exception ex = assertThrows(ValidationException.class, () -> service.aggiungiMembroManuale("CF123", input));
        assertEquals("Sesso non valido", ex.getMessage());
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void testAssistenzaNonDefinita_TC_M_03_10() {
        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .minorenne(false)
                .build();

        Exception ex = assertThrows(ValidationException.class, () -> service.aggiungiMembroManuale("CF123", input));
        assertEquals("Campo Assistenza non definito", ex.getMessage());
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void testMinoreNonDefinito_TC_M_03_11() {
        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .assistenza(true)
                .build();

        Exception ex = assertThrows(ValidationException.class, () -> service.aggiungiMembroManuale("CF123", input));
        assertEquals("Campo Minore di 14 non definito", ex.getMessage());
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void testInserimentoCorretto_TC_M_03_12() throws Exception {
        NucleoFamiliareEntity nucleo = NucleoFamiliareEntity.builder().id(1L).build();
        UREntity admin = UREntity.builder().codiceFiscale("CF123").nucleoFamiliare(nucleo).build();

        when(urRepository.findByCodiceFiscale("CF123")).thenReturn(admin);

        MembroEntity input = MembroEntity.builder()
                .nome("Sandro")
                .cognome("Capri")
                .codiceFiscale("CDT02DGE34FE4rgh")
                .dataDiNascita("10/02/1974")
                .sesso("M")
                .assistenza(true)
                .minorenne(false)
                .build();

        when(repositoryMock.save(any())).thenReturn(input);

        service.aggiungiMembroManuale("CF123", input);

        assertNotNull(input);
        assertEquals("Sandro", input.getNome());
        verify(repositoryMock, times(1)).save(input);
    }
}
