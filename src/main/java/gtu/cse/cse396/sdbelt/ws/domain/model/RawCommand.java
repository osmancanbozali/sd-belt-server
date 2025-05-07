package gtu.cse.cse396.sdbelt.ws.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Builder;

@Builder
public record RawCommand(
                CommandType type,
                JsonNode data) {

        public static final <T> RawCommand of(Command<T> command) {
                return RawCommand.builder()
                                .type(command.type())
                                .data(command.getContent())
                                .build();
        }
}
