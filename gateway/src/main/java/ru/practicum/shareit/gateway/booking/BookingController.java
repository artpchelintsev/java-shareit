package ru.practicum.shareit.gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.client.ShareItClient;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final ShareItClient shareItClient;

    @PostMapping
    public Mono<ResponseEntity<BookingDto>> createBooking(
            @RequestBody BookingDto.BookingRequest bookingRequest,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating booking for user {}: {}", userId, bookingRequest);
        return shareItClient.post("/bookings", bookingRequest, BookingDto.class, userId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error creating booking: {}", error.getMessage()));
    }

    @PatchMapping("/{bookingId}")
    public Mono<ResponseEntity<BookingDto>> approveBooking(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Approving booking {} with approved={} by user {}", bookingId, approved, userId);
        String path = "/bookings/" + bookingId + "?approved=" + approved;
        return shareItClient.patch(path, null, BookingDto.class, userId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error approving booking: {}", error.getMessage()));
    }

    @GetMapping("/{bookingId}")
    public Mono<ResponseEntity<BookingDto>> getBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting booking {} for user {}", bookingId, userId);
        return shareItClient.get("/bookings/" + bookingId, BookingDto.class, userId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error getting booking: {}", error.getMessage()));
    }

    @GetMapping
    public Mono<ResponseEntity<List<BookingDto>>> getUserBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting user bookings for user {} with state {}", userId, state);
        String path = String.format("/bookings?state=%s&from=%d&size=%d", state, from, size);
        ParameterizedTypeReference<List<BookingDto>> typeReference =
                new ParameterizedTypeReference<List<BookingDto>>() {
                };
        return shareItClient.get(path, typeReference, userId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error getting user bookings: {}", error.getMessage()));
    }

    @GetMapping("/owner")
    public Mono<ResponseEntity<List<BookingDto>>> getOwnerBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting owner bookings for user {} with state {}", userId, state);
        String path = String.format("/bookings/owner?state=%s&from=%d&size=%d", state, from, size);
        ParameterizedTypeReference<List<BookingDto>> typeReference =
                new ParameterizedTypeReference<List<BookingDto>>() {
                };
        return shareItClient.get(path, typeReference, userId)
                .map(ResponseEntity::ok)
                .doOnError(error -> log.error("Error getting owner bookings: {}", error.getMessage()));
    }
}