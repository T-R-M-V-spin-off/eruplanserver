package it.unisa.eruplanserver.IS.Repository.GPE;

import it.unisa.eruplanserver.IS.Entity.GPE.PianoEvacuazioneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GPERepository extends JpaRepository<PianoEvacuazioneEntity, Long> {
    boolean existsByNome(String nome);
}