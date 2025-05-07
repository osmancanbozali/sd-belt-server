package gtu.cse.cse396.sdbelt.ws.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Builder;

@Builder
public record RawMessage(
        Long id,
        MessageType type,
        JsonNode content,
        long timestamp) {

    public static final <T> RawMessage ofCommand(Command<T> command) {
        return RawMessage.builder()
                .id(generateId())
                .type(MessageType.COMMAND)
                .content(command.getContent())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static final <T> RawMessage ofEvent(Event<T> event) {
        return RawMessage.builder()
                .id(generateId())
                .type(MessageType.EVENT)
                .content(event.getContent())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    private static Long generateId() {
        return System.currentTimeMillis();
    }

    public final String serialize() {
        return String.format("{\"id\":%d,\"type\":\"%s\",\"content\":%s,\"timestamp\":%d}",
                id, type.getType(), content.toString(), timestamp);
    }
}