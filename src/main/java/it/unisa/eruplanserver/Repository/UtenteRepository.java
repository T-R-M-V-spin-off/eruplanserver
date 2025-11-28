package it.unisa.eruplanserver.Repository;

import it.unisa.eruplanserver.Entity.UtenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtenteRepository extends JpaRepository<UtenteEntity, String> {

    // Trova un utente per email (query derivata da metodo)
    UtenteEntity findByEmail(String email);

    // Trova tutti gli utenti che hanno un certo nome
    List<UtenteEntity> findByNome(String nome);

    // Query JPQL personalizzata: trova utenti con almeno un ordine
    @Query("SELECT u FROM UtenteEntity u WHERE SIZE(u.ordini) > 0")
    List<UtenteEntity> findUtentiConOrdini();

    // Query JPQL con join fetch per prendere utente con profilo
    @Query("SELECT u FROM UtenteEntity u JOIN FETCH u.profilo WHERE u.email = :email")
    UtenteEntity findUtenteConProfilo(String email);
}