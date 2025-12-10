import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MembroServiceTest {

    private MembroRepository repositoryMock;
    private MembroService service;

    @BeforeEach
    void setUp() {
        repositoryMock = mock(MembroRepository.class);
        service = new MembroService(repositoryMock);
    }

    @Test
    void testDataNascitaFormatoErrato_TC_M_03_8() {
        Membro input = new Membro("Sandro", "Capri", "CDT02DGE34FE4rgh", "10/02/197", "M", true, false);

        Exception ex = assertThrows(ValidationException.class, () -> service.aggiungiMembro(input));
        assertEquals("Formato data non valido", ex.getMessage());
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void testSessoNonValido_TC_M_03_9() {
        Membro input = new Membro("Sandro", "Capri", "CDT02DGE34FE4rgh", "10/02/1974", "X", true, false);

        Exception ex = assertThrows(ValidationException.class, () -> service.aggiungiMembro(input));
        assertEquals("Sesso non valido", ex.getMessage());
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void testAssistenzaNonDefinita_TC_M_03_10() {
        Membro input = new Membro("Sandro", "Capri", "CDT02DGE34FE4rgh", "10/02/1974", "M", null, false);

        Exception ex = assertThrows(ValidationException.class, () -> service.aggiungiMembro(input));
        assertEquals("Campo Assistenza non definito", ex.getMessage());
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void testMinoreNonDefinito_TC_M_03_11() {
        Membro input = new Membro("Sandro", "Capri", "CDT02DGE34FE4rgh", "10/02/1974", "M", true, null);

        Exception ex = assertThrows(ValidationException.class, () -> service.aggiungiMembro(input));
        assertEquals("Campo Minore di 14 non definito", ex.getMessage());
        verify(repositoryMock, never()).save(any());
    }

    @Test
    void testInserimentoCorretto_TC_M_03_12() {
        Membro input = new Membro("Sandro", "Capri", "CDT02DGE34FE4rgh", "10/02/1974", "M", true, false);

        when(repositoryMock.save(any())).thenReturn(input);

        Membro result = service.aggiungiMembro(input);

        assertNotNull(result);
        assertEquals("Sandro", result.getNome());
        verify(repositoryMock, times(1)).save(input);
    }
}
