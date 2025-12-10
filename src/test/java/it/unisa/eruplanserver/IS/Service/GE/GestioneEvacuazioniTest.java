package eruplan.unisa.eruplan.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GestioneEvacuazioniTest {

    /* ===========================
       TC-M-20.1: lunghezza del Codice Fiscale
       =========================== */
    @Test
    void invalidLength() {
        // Codice fiscale con lunghezza != 16
        String cf = "GRT36TGHH53"; // 11 caratteri

        assertFalse(Validator.isCodiceFiscaleLengthValid(cf),
                "Atteso false: lunghezza non valida");
    }

    /* ===========================
       TC-M-20.2: caratteri del Codice Fiscale
       =========================== */
    @Test
    void invalidCharacters() {
        // Codice fiscale lungo 16 ma contiene simboli non ammessi
        String cf = "GRT36T$%GHH5334G";

        assertFalse(Validator.isCodiceFiscaleCharactersValid(cf),
                "Atteso false: caratteri non validi");
    }
}
