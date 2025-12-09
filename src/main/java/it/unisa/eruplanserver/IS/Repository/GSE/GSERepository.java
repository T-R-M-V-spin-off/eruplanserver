package it.unisa.eruplanserver.IS.Repository.GSE;


import it.unisa.eruplanserver.IS.Entity.GSE.GestioneStoricoEvacuazioniEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GSERepository extends JpaRepository<GestioneStoricoEvacuazioniEntity, Long> {

    List<GestioneStoricoEvacuazioniEntity> findByPianoId(Long pianoId);
}
