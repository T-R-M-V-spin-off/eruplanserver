package it.unisa.eruplanserver.IS.Service.GE;

import it.unisa.eruplanserver.IS.Entity.GUM.UREntity;
import it.unisa.eruplanserver.IS.Repository.GE.GestioneEvacuazione;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GEService {

    @Autowired
    private GestioneEvacuazione geRepository;

    public String segnalaSalvo(String cf) throws Exception {

        UREntity utente = geRepository.findByCodiceFiscale(cf);


        if (utente == null) {
            throw new Exception("La segnalazione degli utenti arrivati nella zona sicura non viene effettuata dato che il campo “CodiceFiscale” non ha nessuna corrispondenza con un utente sul DB.");
        }
        return "La segnalazione degli utenti arrivati nella zona sicura viene effettuata con successo";
    }
}