package it.unisa.eruplanserver.IS.Utility;

import it.unisa.eruplanserver.IS.Entity.GPE.Punto;
import it.unisa.eruplanserver.IS.Entity.GPE.ZonaSicura;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Validator {

    /*
     * REGEX E PATTERN it.unisa.eruplanserver.Repository.GUM
     */
    private static final String SESSO_REGEX = "^[MF]$";
    private static final Pattern SESSO_PATTERN = Pattern.compile(SESSO_REGEX);

    /*
     * REGEX E PATTERN GPE (Nuovi)
     */
    private static final String NOME_PIANO_REGEX = "^[a-zA-Z0-9\\s]{3,35}$";
    private static final Pattern NOME_PIANO_PATTERN = Pattern.compile(NOME_PIANO_REGEX);

    // REGEX e PATTERN Indirizzi
    private static final String CAP_REGEX = "^\\d{5}$";
    private static final String CIVICO_REGEX = "^[0-9a-zA-Z/\\s]{1,6}$"; //
    private static final Pattern CAP_PATTERN = Pattern.compile(CAP_REGEX);
    private static final Pattern CIVICO_PATTERN = Pattern.compile(CIVICO_REGEX);
    private static final String PAESE_REGEX = "^\\p{L}{4,40}$";
    private static final Pattern PAESE_PATTERN = Pattern.compile(PAESE_REGEX);
    private static final String PROVINCIA_REGEX = "^\\p{L}{4,20}$";
    private static final Pattern PROVINCIA_PATTERN = Pattern.compile(PROVINCIA_REGEX);
    private static final String REGIONE_REGEX = "^\\p{L}{5,25}$";
    private static final Pattern REGIONE_PATTERN = Pattern.compile(REGIONE_REGEX);
    private static final String COMUNE_REGEX = "^\\p{L}{2,40}$";
    private static final Pattern COMUNE_PATTERN = Pattern.compile(COMUNE_REGEX);
    private static final String VIA_REGEX = "^[a-zA-Z0-9\\s]{1,40}$";
    private static final Pattern VIA_PATTERN = Pattern.compile(VIA_REGEX);

    /*
     * REGEX E PATTERN COMUNI
     */
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final String NOME_PERSONA_REGEX = "^[a-zA-Z\\s]{2,30}$";
    private static final Pattern NOME_PERSONA_PATTERN = Pattern.compile(NOME_PERSONA_REGEX);
    private static final String CODICE_FISCALE_REGEX = "^[A-Za-z0-9]{16}$";
    private static final Pattern CODICE_FISCALE_PATTERN = Pattern.compile(CODICE_FISCALE_REGEX);
    private static final String COGNOME_PERSONA_REGEX="^[a-zA-Z\\s]{2,30}$";
    private static final Pattern COGNOME_PERSONA_PATTERN = Pattern.compile(COGNOME_PERSONA_REGEX);

    // Nuovo pattern per TD-M-20: 16 caratteri, solo A-Za-z e cifre 1-9 (ZERO ESCLUSO)
    private static final String CODICE_FISCALE_CHARS_REGEX = "^[A-Za-z1-9]{16}$";
    private static final Pattern CODICE_FISCALE_CHARS_PATTERN = Pattern.compile(CODICE_FISCALE_CHARS_REGEX);

    /*
     * METODI DI VALIDAZIONE
     */
    public static boolean isNomeValid(String nome) {
        if (nome == null || nome.compareTo("") == 0) return false;
        return NOME_PERSONA_PATTERN.matcher(nome).matches();
    }

    public static boolean isCodiceFiscaleValid(String cf) {
        if (cf == null || cf.compareTo("") == 0) return false;
        return CODICE_FISCALE_PATTERN.matcher(cf).matches();
    }

    public static boolean isSessoValid(String sesso) {
        if (sesso == null || sesso.compareTo("") == 0) return false;
        return SESSO_PATTERN.matcher(sesso).matches();
    }

    public static boolean isPasswordValid(String password) {
        if (password == null || password.compareTo("") == 0) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    public static boolean isCognomeValid(String cognome){
        if (cognome == null || cognome.compareTo("") == 0) return false;
        return COGNOME_PERSONA_PATTERN.matcher(cognome).matches();
    }

    public static boolean isDataNascitaValid(LocalDate data) {
        return data != null && data.isBefore(LocalDate.now());
    }

    /**
     * Verifica se il nome del piano di evacuazione è valido.
     *
     * @param nomePiano Il nome da verificare
     * @return true se valido, false altrimenti
     */
    public static boolean isNomePianoValid(String nomePiano) {
        if (nomePiano == null || nomePiano.trim().isEmpty()) {
            return false;
        }
        return NOME_PIANO_PATTERN.matcher(nomePiano.trim()).matches();
    }

    public static boolean isIndirizzoValid(String via, String civico, String comune, String cap, String provincia, String regione, String paese) {
        if (via == null || !VIA_PATTERN.matcher(via).matches()) return false;
        if (civico == null || !CIVICO_PATTERN.matcher(civico).matches()) return false;
        if (comune == null || !COMUNE_PATTERN.matcher(comune).matches()) return false;
        if (cap == null || !CAP_PATTERN.matcher(cap).matches()) return false;
        if (provincia == null || !PROVINCIA_PATTERN.matcher(provincia).matches()) return false;
        if (regione == null || !REGIONE_PATTERN.matcher(regione).matches()) return false;
        if (paese == null || !PAESE_PATTERN.matcher(paese).matches()) return false;

        return true;
    }

    /**
     * Valida il formato della data di nascita nel formato dd/MM/yyyy
     *
     * @param data La data in formato stringa
     * @return true se il formato è valido, false altrimenti
     */
    public static boolean isDataNascitaFormatoValid(String data) {
        if (data == null || data.trim().isEmpty()) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            formatter.parse(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida se il campo assistenza è definito (non null)
     *
     * @param assistenza Il valore boolean del campo assistenza
     * @return true se il campo è stato impostato, false altrimenti
     */
    public static boolean isAssistenzaDefinita(Boolean assistenza) {
        return assistenza != null;
    }

    /**
     * Valida se il campo minorenne è definito (non null)
     *
     * @param minorenne Il valore boolean del campo minorenne
     * @return true se il campo è stato impostato, false altrimenti
     */
    public static boolean isMinorenneDefinito(Boolean minorenne) {
        return minorenne != null;
    }


    public void creaZoneSicure(List<ZonaSicura> zone) throws IllegalArgumentException {
        if (zone == null || zone.isEmpty()) throw new IllegalArgumentException("Lista vuota");

        for (ZonaSicura z : zone) {
            if (z.getRaggio() < 50) {
                throw new IllegalArgumentException("La creazione della zona sicura non viene effettuata dato che per il campo \"ListaZoneSicure\" per uno dei punti il raggio è troppo corto.");
            }
            if (z.getRaggio() > 250) {
                throw new IllegalArgumentException("La creazione della zona sicura non viene effettuata dato che per il campo \"ListaZoneSicure\" per uno dei punti il raggio è troppo grande.");
            }
        }
    }

    public void creaZonaPericolo(List<Punto> punti) throws IllegalArgumentException {
        if (punti == null || punti.size() <= 3) {
            throw new IllegalArgumentException("La creazione della zona di pericolo non viene effettuata dato che il campo “ZonaPericolo” è composto da un numero di punti troppo basso.");
        }
        checkConnessione(punti);
    }

    public void checkConnessione(List<Punto> punti) throws IllegalArgumentException {
        Punto primo = punti.get(0);
        Punto ultimo = punti.get(punti.size() - 1);

        // Se il primo e l'ultimo punto NON coincidono, il poligono è aperto.
        // Dobbiamo capire PERCHÉ è aperto per dare il messaggio giusto.
        if (!primo.equals(ultimo)) {

            Set<Punto> set = new HashSet<>(punti);

            // Caso TC-W-17.3: Nessun punto è ripetuto (Set size == List size).
            // Esempio: A -> B -> C -> D. Il primo punto "A" non è collegato a nessun altro (D).
            if (set.size() == punti.size()) {
                throw new IllegalArgumentException("La creazione della zona di pericolo non viene effettuata dato che nel campo “ZonaPericolo” il primo punto non è collegato a nessun altro.");
            }

            // Caso TC-W-17.2: Ci sono punti ripetuti, ma l'ultimo non è il primo.
            // Esempio: A -> B -> C -> B. Il poligono si chiude su B, non su A.
            else {
                throw new IllegalArgumentException("La creazione della zona di pericolo non viene effettuata dato che nel campo “ZonaPericolo” l’ultimo punto della lista non è collegato al primo.");
            }
        }
    }

    public static boolean isCodiceFiscaleLengthValid(String cf) {
        return cf != null && cf.length() == 16;
    }

    public static boolean isCodiceFiscaleCharactersValid(String cf) {
        if (cf == null) return false;
        return CODICE_FISCALE_CHARS_PATTERN.matcher(cf).matches();
    }
}