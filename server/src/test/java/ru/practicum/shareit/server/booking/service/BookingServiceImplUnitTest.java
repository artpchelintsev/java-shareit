package ru.practicum.shareit.server.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.BookingStatus;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto.BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "Booker", "booker@email.com");
        owner = new User(2L, "Owner", "owner@email.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        bookingRequest = new BookingDto.BookingRequest(
                1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_shouldThrowNotFoundWhenItemNotFound() {
        // Given
        when(userService.getUserById(anyLong())).thenReturn(new UserDto(1L, "Booker", "booker@email.com"));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookingRequest, 1L));
    }

    @Test
    void createBooking_shouldThrowValidationExceptionWhenStartIsNull() {
        // Given
        BookingDto.BookingRequest invalidRequest = new BookingDto.BookingRequest(1L, null, LocalDateTime.now().plusDays(2));
        when(userService.getUserById(anyLong())).thenReturn(new UserDto(1L, "Booker", "booker@email.com"));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // When & Then
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(invalidRequest, 1L));
    }

    @Test
    void createBooking_shouldThrowValidationExceptionWhenEndIsNull() {
        // Given
        BookingDto.BookingRequest invalidRequest = new BookingDto.BookingRequest(1L, LocalDateTime.now().plusDays(1), null);
        when(userService.getUserById(anyLong())).thenReturn(new UserDto(1L, "Booker", "booker@email.com"));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // When & Then
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(invalidRequest, 1L));
    }

    @Test
    void createBooking_shouldThrowValidationExceptionWhenStartEqualsEnd() {
        // Given
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        BookingDto.BookingRequest invalidRequest = new BookingDto.BookingRequest(1L, sameTime, sameTime);
        when(userService.getUserById(anyLong())).thenReturn(new UserDto(1L, "Booker", "booker@email.com"));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // When & Then
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(invalidRequest, 1L));
    }

    @Test
    void approveBooking_shouldThrowNotFoundWhenBookingNotFound() {
        // Given
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(1L, true, 2L));
    }

    @Test
    void approveBooking_shouldThrowValidationExceptionWhenAlreadyProcessed() {
        // Given
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // When & Then
        assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(1L, true, 2L));
    }

    @Test
    void getBookingById_shouldThrowNotFoundWhenBookingNotFound() {
        // Given
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L));
    }

    @Test
    void getUserBookings_shouldReturnCurrentBookings() {
        // Given
        when(userService.getUserById(anyLong())).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findCurrentByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // When
        List<BookingDto> result = bookingService.getUserBookings(1L, "CURRENT", 0, 10);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_shouldReturnPastBookings() {
        // Given
        when(userService.getUserById(anyLong())).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findPastByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // When
        List<BookingDto> result = bookingService.getUserBookings(1L, "PAST", 0, 10);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_shouldReturnFutureBookings() {
        // Given
        when(userService.getUserById(anyLong())).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findFutureByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // When
        List<BookingDto> result = bookingService.getUserBookings(1L, "FUTURE", 0, 10);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_shouldReturnRejectedBookings() {
        // Given
        when(userService.getUserById(anyLong())).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), eq(BookingStatus.REJECTED), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // When
        List<BookingDto> result = bookingService.getUserBookings(1L, "REJECTED", 0, 10);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_shouldReturnCurrentBookings() {
        // Given
        when(userService.getUserById(anyLong())).thenReturn(new UserDto(1L, "Owner", "owner@email.com"));
        when(bookingRepository.findCurrentByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // When
        List<BookingDto> result = bookingService.getOwnerBookings(1L, "CURRENT", 0, 10);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
