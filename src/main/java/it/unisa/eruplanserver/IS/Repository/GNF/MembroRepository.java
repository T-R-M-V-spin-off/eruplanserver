package it.unisa.eruplanserver.IS.Repository.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.MembroEntity;
import it.unisa.eruplanserver.IS.Entity.GNF.NucleoFamiliareEntity; // <--- AGGIUNGI QUESTO IMPORT
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // <--- AGGIUNGI SE MANCA
import java.util.List; // <--- AGGIUNGI
import java.util.Optional; // <--- AGGIUNGI

@Repository
public interface MembroRepository extends JpaRepository<MembroEntity, Long> {

    // 1. Serve per trovare l'utente loggato partendo dal suo CF (che è nella sessione)
    // Spring traduce in: SELECT * FROM membro WHERE codice_fiscale = ?
    Optional<MembroEntity> findByCodiceFiscale(String codiceFiscale);

    // 2. Serve per trovare tutti i membri di quel nucleo
    // Spring traduce in: SELECT * FROM membro WHERE nucleo_id = ?
    List<MembroEntity> findByNucleoFamiliare(NucleoFamiliareEntity nucleoFamiliare);

}