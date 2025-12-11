package it.unisa.eruplanserver.IS.Repository.GE;

import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GestioneEvacuazione extends JpaRepository<UREntity, Long> {

    UREntity findByCodiceFiscale(String cf);
}