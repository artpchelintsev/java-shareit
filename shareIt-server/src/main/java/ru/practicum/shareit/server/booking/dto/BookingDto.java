package ru.practicum.shareit.server.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private Booker booker;
    private Item item;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Booker {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String name;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingRequest {
        private Long itemId;
        private LocalDateTime start;
        private LocalDateTime end;
    }
}