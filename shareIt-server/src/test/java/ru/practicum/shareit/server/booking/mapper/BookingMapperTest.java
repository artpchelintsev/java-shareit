package ru.practicum.shareit.server.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.BookingStatus;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void toBookingDto_shouldConvertBookingToDto() {
        // Given
        User booker = new User();
        booker.setId(1L);
        booker.setName("Booker");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        // When
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        // Then
        assertNotNull(bookingDto);
        assertEquals(1L, bookingDto.getId());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertEquals(1L, bookingDto.getBooker().getId());
        assertEquals("Booker", bookingDto.getBooker().getName());
        assertEquals(1L, bookingDto.getItem().getId());
        assertEquals("Item", bookingDto.getItem().getName());
    }

    @Test
    void toBookingShortDto_shouldConvertBookingToShortDto() {
        // Given
        User booker = new User();
        booker.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);

        // When
        var shortDto = BookingMapper.toBookingShortDto(booking);

        // Then
        assertNotNull(shortDto);
        assertEquals(1L, shortDto.getId());
        assertEquals(1L, shortDto.getBookerId());
    }

    @Test
    void toBookingDto_shouldHandleNullBooking() {
        // When
        BookingDto bookingDto = BookingMapper.toBookingDto(null);

        // Then
        assertNull(bookingDto);
    }

    @Test
    void toBookingShortDto_shouldHandleNullBooking() {
        // When
        var shortDto = BookingMapper.toBookingShortDto(null);

        // Then
        assertNull(shortDto);
    }
}