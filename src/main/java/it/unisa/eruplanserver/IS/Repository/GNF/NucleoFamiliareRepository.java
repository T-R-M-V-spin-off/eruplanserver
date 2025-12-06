package it.unisa.eruplanserver.IS.Repository.GNF;

import it.unisa.eruplanserver.IS.Entity.GNF.NucleoFamiliareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NucleoFamiliareRepository extends JpaRepository<NucleoFamiliareEntity, Long> {
}