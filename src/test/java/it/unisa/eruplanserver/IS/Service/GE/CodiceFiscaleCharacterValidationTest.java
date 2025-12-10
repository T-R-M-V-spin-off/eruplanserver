package eruplan.unisa.eruplan.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodiceFiscaleCharacterValidationTest {

    @Test
    void invalidCharacters() {
        // TC-M-20.2: contiene simboli non ammessi anche se lungo 16
        String cf = "GRT36T$%GHH5334G";

        assertFalse(Validator.isCodiceFiscaleCharactersValid(cf),
                "Atteso false: caratteri non validi");
    }
}