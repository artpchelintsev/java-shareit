package ru.practicum.shareit.gateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.client.ShareItClient;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebFluxTest(BookingController.class)
class BookingControllerGatewayTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShareItClient shareItClient;

    @Test
    void createBooking_shouldReturnBooking() {
        // Given
        BookingDto.BookingRequest bookingRequest = new BookingDto.BookingRequest(
                1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "WAITING",
                new BookingDto.Booker(1L, "Booker"),
                new BookingDto.Item(1L, "Item"));

        when(shareItClient.post(anyString(), any(), eq(BookingDto.class), anyLong()))
                .thenReturn(Mono.just(bookingDto));

        // When & Then
        webTestClient.post()
                .uri("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookingRequest)
                .header("X-Sharer-User-Id", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.status").isEqualTo("WAITING");

        verify(shareItClient, times(1)).post(anyString(), any(), eq(BookingDto.class), eq(1L));
    }

    @Test
    void approveBooking_shouldApproveBooking() {
        // Given
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "APPROVED",
                new BookingDto.Booker(1L, "Booker"),
                new BookingDto.Item(1L, "Item"));

        when(shareItClient.patch(anyString(), isNull(), eq(BookingDto.class), anyLong()))
                .thenReturn(Mono.just(bookingDto));

        // When & Then
        webTestClient.patch()
                .uri("/bookings/1?approved=true")
                .header("X-Sharer-User-Id", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("APPROVED");

        verify(shareItClient, times(1)).patch(anyString(), isNull(), eq(BookingDto.class), eq(1L));
    }
}