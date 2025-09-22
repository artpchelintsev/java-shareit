package ru.practicum.shareit.server.booking.mapper;

import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.item.dto.ItemDto;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        BookingDto.Booker booker = new BookingDto.Booker();
        booker.setId(booking.getBooker().getId());
        booker.setName(booking.getBooker().getName());
        dto.setBooker(booker);

        BookingDto.Item item = new BookingDto.Item();
        item.setId(booking.getItem().getId());
        item.setName(booking.getItem().getName());
        dto.setItem(item);

        return dto;
    }

    public static ItemDto.BookingShortDto toBookingShortDto(Booking booking) {
        ItemDto.BookingShortDto dto = new ItemDto.BookingShortDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }
}