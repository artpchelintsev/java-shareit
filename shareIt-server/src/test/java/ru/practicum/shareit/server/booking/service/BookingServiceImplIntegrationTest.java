package ru.practicum.shareit.server.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.ShareItServerApp;
import ru.practicum.shareit.server.booking.BookingStatus;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServerApp.class)
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private Long ownerId;
    private Long bookerId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        UserDto ownerDto = new UserDto(null, "Owner", "owner@email.com");
        UserDto bookerDto = new UserDto(null, "Booker", "booker@email.com");

        UserDto createdOwner = userService.createUser(ownerDto);
        UserDto createdBooker = userService.createUser(bookerDto);

        ownerId = createdOwner.getId();
        bookerId = createdBooker.getId();

        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description", true, null, null, null, null);
        ItemDto createdItem = itemService.createItem(itemDto, ownerId);
        itemId = createdItem.getId();
    }

    @Test
    void createBooking_shouldCreateBookingSuccessfully() {
        // Given
        BookingDto.BookingRequest bookingRequest = new BookingDto.BookingRequest(
                itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        // When
        BookingDto createdBooking = bookingService.createBooking(bookingRequest, bookerId);

        // Then
        assertNotNull(createdBooking.getId());
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());
        assertEquals(bookerId, createdBooking.getBooker().getId());
        assertEquals(itemId, createdBooking.getItem().getId());
    }

    @Test
    void createBooking_shouldThrowExceptionWhenBookingOwnItem() {
        // Given
        BookingDto.BookingRequest bookingRequest = new BookingDto.BookingRequest(
                itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        // When & Then
        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookingRequest, ownerId));
    }
}