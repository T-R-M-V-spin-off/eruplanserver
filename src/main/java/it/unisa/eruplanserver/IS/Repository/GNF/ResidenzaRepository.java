package it.unisa.eruplanserver.IS.Repository.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.ResidenzaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidenzaRepository extends JpaRepository<ResidenzaEntity, Long> {
}

