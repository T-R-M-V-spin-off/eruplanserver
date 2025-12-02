package it.unisa.eruplanserver.IS.Repository.GUM;

import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface URRepository extends JpaRepository<UREntity, Long> {

    /**
     * Cerca un cittadino (UR) nel database tramite il Codice Fiscale.
     *
     * @param codiceFiscale Il codice fiscale dell'utente.
     * @return L'entità UREntity se trovata, altrimenti null.
     */
    UREntity findByCodiceFiscale(String codiceFiscale);
    /**
     * Verifica se esiste già qualcuno con questo CF
     *
     * @param codiceFiscale Il codice fiscale da verificare
     * @return true se già esiste, altrimenti false
     */
    boolean existsByCodiceFiscale(String codiceFiscale);

}
