package it.unisa.eruplanserver.IS.Service.GSE;

import it.unisa.eruplanserver.IS.Entity.GSE.GestioneStoricoEvacuazioniEntity;
import it.unisa.eruplanserver.IS.Exception.GSE.GSEException;
import it.unisa.eruplanserver.IS.Repository.GSE.GSERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GSEServiceImpl implements GSEService {

    @Autowired
    private GSERepository partecipazioneRepository;

    @Override
    public List<GestioneStoricoEvacuazioniEntity> getPartecipantiPiano(Long idPiano) throws GSEException {
        try {
            // se l'ID è nullo o negativo, lancia eccezione
            if (idPiano == null || idPiano <= 0) {
                throw new GSEException("ID Piano non valido.");
            }

            // Recupera dati
            return partecipazioneRepository.findByPianoId(idPiano);

        } catch (Exception e) {
            throw new GSEException("Errore durante il recupero dei partecipanti: " + e.getMessage());
        }
    }
}
