package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingEntityTest {

    @Test
    void bookingConstructor_shouldCreateBookingWithParameters() {
        // Given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Item item = new Item();
        User booker = new User();
        BookingStatus status = BookingStatus.WAITING;

        // When
        Booking booking = new Booking(1L, start, end, item, booker, status);

        // Then
        assertEquals(1L, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(status, booking.getStatus());
    }

    @Test
    void bookingSetters_shouldSetAllFields() {
        // Given
        Booking booking = new Booking();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Item item = new Item();
        User booker = new User();
        BookingStatus status = BookingStatus.APPROVED;

        // When
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);

        // Then
        assertEquals(1L, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(status, booking.getStatus());
    }


    @Test
    void bookingToString_shouldReturnStringRepresentation() {
        // Given
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                new Item(), new User(), BookingStatus.WAITING);

        // When
        String toString = booking.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("WAITING"));
    }
}