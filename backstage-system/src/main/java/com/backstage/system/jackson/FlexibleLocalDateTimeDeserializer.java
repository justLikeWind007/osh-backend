package com.backstage.system.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter MILLIS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        }

        if (parser.currentToken().isNumeric()) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(parser.getLongValue()), ZoneId.systemDefault());
        }

        String text = parser.getValueAsString();
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        return parse(text.trim());
    }

    private LocalDateTime parse(String text) throws IOException {
        try {
            return OffsetDateTime.parse(text).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDateTime.parse(text, DEFAULT_FORMATTER);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDateTime.parse(text, MILLIS_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new IOException("Cannot parse LocalDateTime value: " + text, ex);
        }
    }
}
