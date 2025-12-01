package it.unisa.eruplanserver.IS.Repository.GUW;

import it.unisa.eruplanserver.IS.Entity.GUW.OPCEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OPCRepository extends JpaRepository<OPCEntity, Long> {

    /**
     * Cerca un operatore nel database tramite il Codice Fiscale.
     *
     * @param codiceFiscale Il codice fiscale dell'operatore.
     * @return L'entità OPCEntity se trovata, altrimenti null.
     */
    OPCEntity findByCodiceFiscale(String codiceFiscale);
}