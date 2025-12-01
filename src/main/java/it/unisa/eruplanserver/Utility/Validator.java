package it.unisa.eruplanserver.Utility;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Validator {

    /*
     * REGEX E PATTERN GUM
     */

    private static final String SESSO_REGEX = "^[MF]$";
    private static final Pattern SESSO_PATTERN = Pattern.compile(SESSO_REGEX);

    /*
     * REGEX E PATTERN COMUNI
     */

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final String NOME_PERSONA_REGEX = "^[a-zA-Z\\s]{2,30}$";
    private static final Pattern NOME_PERSONA_PATTERN = Pattern.compile(NOME_PERSONA_REGEX);
    private static final String CODICE_FISCALE_REGEX = "^[A-Za-z0-9]{16}$";
    private static final Pattern CODICE_FISCALE_PATTERN = Pattern.compile(CODICE_FISCALE_REGEX);


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

    public static boolean isDataNascitaValid(LocalDate data) {
        return data != null && data.isBefore(LocalDate.now());
    }
}