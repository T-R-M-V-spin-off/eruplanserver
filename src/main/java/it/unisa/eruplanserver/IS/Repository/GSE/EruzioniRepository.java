package it.unisa.eruplanserver.IS.Repository.GSE;

import it.unisa.eruplanserver.IS.Entity.GSE.EruzioneVulcanicaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository public interface EruzioniRepository extends JpaRepository<EruzioneVulcanicaEntity,Long> {
    //ordinamento crescente per data
    List <EruzioneVulcanicaEntity> findAllByOrderByDataInizioAsc();

    //ordinamento decrescente per data
    List <EruzioneVulcanicaEntity> findAllByOrderByDataInizioDesc();


}