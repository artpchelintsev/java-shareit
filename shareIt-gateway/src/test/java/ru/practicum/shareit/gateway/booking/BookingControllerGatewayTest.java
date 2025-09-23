package ru.practicum.shareit.gateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.client.ShareItClient;

import java.time.LocalDateTime;
import java.util.List;

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
    void createBooking_withInvalidData_shouldReturnBadRequest() {
        BookingDto.BookingRequest invalidRequest = new BookingDto.BookingRequest(
                null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        when(shareItClient.post(anyString(), any(), eq(BookingDto.class), anyLong()))
                .thenReturn(Mono.error(new RuntimeException("Validation error")));

        webTestClient.post()
                .uri("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .header("X-Sharer-User-Id", "1")
                .exchange()
                .expectStatus().is5xxServerError();

        verify(shareItClient, times(1)).post(anyString(), any(), eq(BookingDto.class), eq(1L));
    }

    @Test
    void createBooking_withoutUserId_shouldReturnError() {
        BookingDto.BookingRequest bookingRequest = new BookingDto.BookingRequest(
                1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        webTestClient.post()
                .uri("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookingRequest)
                .exchange()
                .expectStatus().is5xxServerError();

        verify(shareItClient, never()).post(anyString(), (Object) any(), (Class<Object>) any(), anyLong());
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

    @Test
    void approveBooking_withFalse_shouldRejectBooking() {
        // Given
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "REJECTED",
                new BookingDto.Booker(1L, "Booker"),
                new BookingDto.Item(1L, "Item"));

        when(shareItClient.patch(anyString(), isNull(), eq(BookingDto.class), anyLong()))
                .thenReturn(Mono.just(bookingDto));

        // When & Then
        webTestClient.patch()
                .uri("/bookings/1?approved=false")
                .header("X-Sharer-User-Id", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("REJECTED");

        verify(shareItClient, times(1)).patch(anyString(), isNull(), eq(BookingDto.class), eq(1L));
    }

    @Test
    void getBooking_shouldReturnBooking() {
        // Given
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "WAITING",
                new BookingDto.Booker(1L, "Booker"),
                new BookingDto.Item(1L, "Item"));

        when(shareItClient.get(eq("/bookings/1"), eq(BookingDto.class), anyLong()))
                .thenReturn(Mono.just(bookingDto));

        // When & Then
        webTestClient.get()
                .uri("/bookings/1")
                .header("X-Sharer-User-Id", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);

        verify(shareItClient, times(1)).get(eq("/bookings/1"), eq(BookingDto.class), eq(1L));
    }

    @Test
    void getUserBookings_shouldReturnList() {
        // Given
        BookingDto booking1 = new BookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "WAITING",
                new BookingDto.Booker(1L, "Booker"),
                new BookingDto.Item(1L, "Item"));

        BookingDto booking2 = new BookingDto(2L,
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4),
                "APPROVED",
                new BookingDto.Booker(1L, "Booker"),
                new BookingDto.Item(2L, "Item2"));

        List<BookingDto> bookings = List.of(booking1, booking2);

        ParameterizedTypeReference<List<BookingDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        when(shareItClient.get(contains("/bookings?state=ALL"), eq(typeRef), anyLong()))
                .thenReturn(Mono.just(bookings));

        // When & Then
        webTestClient.get()
                .uri("/bookings?state=ALL&from=0&size=10")
                .header("X-Sharer-User-Id", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookingDto.class)
                .hasSize(2);

        verify(shareItClient, times(1)).get(anyString(), eq(typeRef), eq(1L));
    }

    @Test
    void getUserBookings_withDifferentStates_shouldWork() {
        // Given
        ParameterizedTypeReference<List<BookingDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        when(shareItClient.get(anyString(), eq(typeRef), anyLong()))
                .thenReturn(Mono.just(List.of()));

        // When & Then - test different states
        webTestClient.get()
                .uri("/bookings?state=CURRENT&from=0&size=10")
                .header("X-Sharer-User-Id", "1")
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/bookings?state=PAST&from=0&size=10")
                .header("X-Sharer-User-Id", "1")
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/bookings?state=FUTURE&from=0&size=10")
                .header("X-Sharer-User-Id", "1")
                .exchange()
                .expectStatus().isOk();

        verify(shareItClient, times(3)).get(anyString(), eq(typeRef), eq(1L));
    }

    @Test
    void getOwnerBookings_shouldReturnList() {
        // Given
        BookingDto booking1 = new BookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "WAITING",
                new BookingDto.Booker(1L, "Booker"),
                new BookingDto.Item(1L, "Item"));

        List<BookingDto> bookings = List.of(booking1);

        ParameterizedTypeReference<List<BookingDto>> typeRef = new ParameterizedTypeReference<>() {
        };
        when(shareItClient.get(contains("/bookings/owner"), eq(typeRef), anyLong()))
                .thenReturn(Mono.just(bookings));

        // When & Then
        webTestClient.get()
                .uri("/bookings/owner?state=ALL&from=0&size=10")
                .header("X-Sharer-User-Id", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookingDto.class)
                .hasSize(1);

        verify(shareItClient, times(1)).get(anyString(), eq(typeRef), eq(1L));
    }

    @Test
    void getBooking_whenNotFound_shouldReturnError() {
        // Given
        when(shareItClient.get(eq("/bookings/999"), eq(BookingDto.class), anyLong()))
                .thenReturn(Mono.error(new RuntimeException("Not found")));

        // When & Then
        webTestClient.get()
                .uri("/bookings/999")
                .header("X-Sharer-User-Id", "1")
                .exchange()
                .expectStatus().is5xxServerError();

        verify(shareItClient, times(1)).get(eq("/bookings/999"), eq(BookingDto.class), eq(1L));
    }
}