package it.unisa.eruplanserver.IS.Service.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.MembroEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.RichiestaAccessoEntity;
import java.util.List;

public interface GNFService {

    /**
     * RF-GNF.01: Invia un invito a un altro utente per unirsi al nucleo.
     * @param cfAdmin Codice fiscale di chi invia l'invito
     * @param cfInvitato Codice fiscale dell'utente da invitare
     */
    void invitaUtente(String cfAdmin, String cfInvitato) throws Exception;

    /**
     * RF-GNF.02: Accetta una richiesta di accesso pendente.
     * @param idRichiesta ID della richiesta da accettare
     */
    void accettaInvito(Long idRichiesta) throws Exception;

    /**
     * RF-GNF.03: Aggiunge un membro manuale (es. minore o anziano senza app) al nucleo.
     * @param cfAdmin Codice fiscale dell'admin del nucleo
     * @param membro L'entità membro con i dati anagrafici
     */
    void aggiungiMembroManuale(String cfAdmin, MembroEntity membro) throws Exception;

    /**
     * RF-GNF.04: Permette a un utente di uscire dal nucleo familiare corrente.
     * @param cfUtente Codice fiscale dell'utente che vuole uscire
     */
    void abbandonaNucleo(String cfUtente) throws Exception;

    /**
     * Recupera la lista delle richieste di accesso in stato PENDING per un utente.
     * @param cfUtente Codice fiscale dell'utente invitato
     * @return Lista di richieste
     */
    List<RichiestaAccessoEntity> getRichiestePendenti(String cfUtente);
}