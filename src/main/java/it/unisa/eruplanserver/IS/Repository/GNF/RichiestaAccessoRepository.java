package it.unisa.eruplanserver.IS.Repository.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.RichiestaAccessoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RichiestaAccessoRepository extends JpaRepository<RichiestaAccessoEntity, Long> {
    List<RichiestaAccessoEntity> findByUtenteInvitatoIdAndStato(Long utenteId, String stato);
}