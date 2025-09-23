package ru.practicum.shareit.server.request.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testItemRequestCreateDtoSerialization() throws JsonProcessingException {
        // Given
        ItemRequestDto.ItemRequestCreateDto createDto =
                new ItemRequestDto.ItemRequestCreateDto("Need a drill");

        // When
        String result = objectMapper.writeValueAsString(createDto);

        // Then
        assertThat(result).isEqualTo("{\"description\":\"Need a drill\"}");
    }

    @Test
    void testItemRequestCreateDtoDeserialization() throws JsonProcessingException {
        // Given
        String content = "{\"description\":\"Need a drill\"}";

        // When
        ItemRequestDto.ItemRequestCreateDto result =
                objectMapper.readValue(content, ItemRequestDto.ItemRequestCreateDto.class);

        // Then
        assertThat(result.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void testItemRequestDtoSerialization() throws JsonProcessingException {
        // Given
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0);
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Need a drill", created, List.of());

        // When
        String result = objectMapper.writeValueAsString(requestDto);

        // Then
        assertThat(result).contains("\"id\":1");
        assertThat(result).contains("\"description\":\"Need a drill\"");
        assertThat(result).contains("\"created\":\"2024-01-01T10:00:00\"");
        assertThat(result).contains("\"items\":[]");
    }

    @Test
    void testItemRequestDtoDeserialization() throws JsonProcessingException {
        // Given
        String content = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2024-01-01T10:00:00\",\"items\":[]}";

        // When
        ItemRequestDto result = objectMapper.readValue(content, ItemRequestDto.class);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(result.getItems()).isEmpty();
    }
}