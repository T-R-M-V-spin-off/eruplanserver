package it.unisa.eruplanserver.IS.Utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisa.eruplanserver.IS.Entity.GPE.ZonaSicura;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ZonaSicuraConverter implements AttributeConverter<ZonaSicura, String> {

    // ObjectMapper è thread-safe, possiamo usarlo statico o di istanza
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ZonaSicura attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // In produzione potresti voler loggare l'errore invece di lanciare RuntimeException
            throw new RuntimeException("Errore durante la serializzazione JSON di ZonaSicura", e);
        }
    }

    @Override
    public ZonaSicura convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, ZonaSicura.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Errore durante la deserializzazione JSON di ZonaSicura", e);
        }
    }
}