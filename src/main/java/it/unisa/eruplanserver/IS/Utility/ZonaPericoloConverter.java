package it.unisa.eruplanserver.IS.Utility; // O il tuo package

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisa.eruplanserver.IS.Entity.GPE.ZonaPericolo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ZonaPericoloConverter implements AttributeConverter<ZonaPericolo, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ZonaPericolo attribute) {
        if (attribute == null) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Errore serializzazione JSON ZonaPericolo", e);
        }
    }

    @Override
    public ZonaPericolo convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        try {
            return objectMapper.readValue(dbData, ZonaPericolo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Errore deserializzazione JSON ZonaPericolo", e);
        }
    }
}