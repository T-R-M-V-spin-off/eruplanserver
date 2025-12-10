package eruplan.unisa.eruplan.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodiceFiscaleLengthValidationTest {

    @Test
    void invalidLength() {
        // TC-M-20.1: codice fiscale con lunghezza != 16
        String cf = "GRT36TGHH53"; // 11 caratteri

        assertFalse(Validator.isCodiceFiscaleLengthValid(cf),
                "Atteso false: lunghezza non valida");
    }
}