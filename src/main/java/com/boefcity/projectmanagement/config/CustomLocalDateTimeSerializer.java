/*package com.boefcity.projectmanagement.config;



import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /*
     * Custom serializer for LocalDateTime felter.
     * Denne serializer sikrer, at LocalDateTime felter formateres
     * uden sekunder, når de serialiseres til JSON.
     */

    /*
    - 'value' Den LocalDateTime, der skal serialiseres. Kan ikke være null.

    - 'gen' JsonGenerator bruges til at skrive JSON-outputtet. Dette er den generator,
       der håndterer serialiseringen af JSON data.

    - 'serializers' SerializerProvider, der kan bruges til at
       at serialisere objekter af andre typer.

     @throws IOException Hvis der opstår en input/output-fejl under processen.

     */
/*
    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeString(formatter.format(value));
        }

    }
}
*/
