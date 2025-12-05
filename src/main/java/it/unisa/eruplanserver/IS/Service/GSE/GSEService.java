package it.unisa.eruplanserver.IS.Service.GSE;

import it.unisa.eruplanserver.IS.Entity.GSE.GestioneStoricoEvacuazioniEntity;
import it.unisa.eruplanserver.IS.Entity.GSE.EruzioneVulcanicaEntity;
import it.unisa.eruplanserver.IS.Exception.GSE.GSEException;

import java.util.List;

public interface GSEService {
    /**
     * Recupera la lista dei partecipanti a un piano con il loro stato di salvezza.
     * @param idPiano ID del piano di evacuazione
     * @return Lista di partecipazioni
     */
    List<GestioneStoricoEvacuazioniEntity> getPartecipantiPiano(Long idPiano) throws GSEException;

    //Visualizzare  la lista delle eruzioni vulcaniche
    List <EruzioneVulcanicaEntity> getListaEruzioni() throws GSEException;

    //ordinarli in maniera crescente
    List <EruzioneVulcanicaEntity>getListaEruzioniCrescente() throws GSEException;

    //ordinarli in maniera decrescente
    List<EruzioneVulcanicaEntity>getListaEruzioniDecrescente() throws GSEException;
}

