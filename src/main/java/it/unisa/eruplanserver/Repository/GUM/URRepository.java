package it.unisa.eruplanserver.Repository.GUM;

import it.unisa.eruplanserver.Entity.GUM.UREntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface URRepository extends JpaRepository<UREntity, Long> {

    UREntity findByCodiceFiscale(String codiceFiscale);

}