package it.unisa.eruplanserver.IS.Utility;

import java.time.LocalDate;
import java.util.Date;
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
        if (via == null || via.length() < 1 || via.length() > 40) return false;
        if (civico == null || !CIVICO_PATTERN.matcher(civico).matches()) return false;
        if (comune == null || comune.length() < 2 || comune.length() > 40) return false;
        if (cap == null || !CAP_PATTERN.matcher(cap).matches()) return false;
        if (provincia == null || provincia.length() < 4 || provincia.length() > 20) return false;
        if (regione == null || regione.length() < 5 || regione.length() > 25) return false;
        if (paese == null || paese.length() < 4 || paese.length() > 40) return false;

        return true;
    }
}