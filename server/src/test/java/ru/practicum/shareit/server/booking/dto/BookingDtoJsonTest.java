package ru.practicum.shareit.server.booking.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.server.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testBookingRequestSerialization() throws JsonProcessingException {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 2, 10, 0);
        BookingDto.BookingRequest request = new BookingDto.BookingRequest(1L, start, end);

        // When
        String result = objectMapper.writeValueAsString(request);

        // Then
        assertThat(result).contains("\"itemId\":1");
        assertThat(result).contains("\"start\":\"2024-01-01T10:00:00\"");
        assertThat(result).contains("\"end\":\"2024-01-02T10:00:00\"");
    }

    @Test
    void testBookingRequestDeserialization() throws JsonProcessingException {
        // Given
        String content = "{\"itemId\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-02T10:00:00\"}";

        // When
        BookingDto.BookingRequest result = objectMapper.readValue(content, BookingDto.BookingRequest.class);

        // Then
        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
    }

    @Test
    void testBookingDtoSerialization() throws JsonProcessingException {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 2, 10, 0);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setStatus(BookingStatus.valueOf("WAITING"));

        // When
        String result = objectMapper.writeValueAsString(bookingDto);

        // Then
        assertThat(result).contains("\"id\":1");
        assertThat(result).contains("\"status\":\"WAITING\"");
        assertThat(result).contains("\"start\":\"2024-01-01T10:00:00\"");
        assertThat(result).contains("\"end\":\"2024-01-02T10:00:00\"");
    }

    @Test
    void testBookingDtoDeserialization() throws JsonProcessingException {
        // Given
        String content = "{\"id\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-02T10:00:00\",\"status\":\"WAITING\"}";

        // When
        BookingDto result = objectMapper.readValue(content, BookingDto.class);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
    }
}