package ru.practicum.shareit.server.booking.service;

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
import ru.practicum.shareit.server.exception.ForbiddenException;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplBranchTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_shouldThrowExceptionWhenItemNotFound() {
        // Given
        BookingDto.BookingRequest bookingRequest = new BookingDto.BookingRequest(
                1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookingRequest, 1L));
    }

    @Test
    void createBooking_shouldThrowExceptionWhenItemNotAvailable() {
        // Given
        BookingDto.BookingRequest bookingRequest = new BookingDto.BookingRequest(
                1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(false);
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.getUserById(anyLong())).thenReturn(new UserDto());

        // When & Then
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingRequest, 1L));
    }

    @Test
    void createBooking_shouldThrowExceptionWhenStartDateIsNull() {
        // Given
        BookingDto.BookingRequest bookingRequest = new BookingDto.BookingRequest(
                1L, null, LocalDateTime.now().plusDays(2));

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.getUserById(anyLong())).thenReturn(new UserDto());

        // When & Then
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingRequest, 1L));
    }

    @Test
    void createBooking_shouldThrowExceptionWhenEndDateIsNull() {
        // Given
        BookingDto.BookingRequest bookingRequest = new BookingDto.BookingRequest(
                1L, LocalDateTime.now().plusDays(1), null);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.getUserById(anyLong())).thenReturn(new UserDto());

        // When & Then
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingRequest, 1L));
    }

    @Test
    void createBooking_shouldThrowExceptionWhenStartAfterEnd() {
        // Given
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingDto.BookingRequest bookingRequest = new BookingDto.BookingRequest(1L, start, end);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.getUserById(anyLong())).thenReturn(new UserDto());

        // When & Then
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingRequest, 1L));
    }

    @Test
    void createBooking_shouldThrowExceptionWhenStartEqualsEnd() {
        // Given
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
        BookingDto.BookingRequest bookingRequest = new BookingDto.BookingRequest(1L, dateTime, dateTime);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.getUserById(anyLong())).thenReturn(new UserDto());

        // When & Then
        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingRequest, 1L));
    }

    @Test
    void approveBooking_shouldThrowExceptionWhenBookingNotFound() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(1L, true, 1L));
    }

    @Test
    void approveBooking_shouldThrowExceptionWhenNotOwner() {
        // Given
        Booking booking = new Booking();
        Item item = new Item();
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // When & Then
        assertThrows(ForbiddenException.class,
                () -> bookingService.approveBooking(1L, true, 1L)); // userId = 1, ownerId = 2
    }

    @Test
    void approveBooking_shouldThrowExceptionWhenStatusNotWaiting() {
        // Given
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED); // Already approved

        Item item = new Item();
        User owner = new User();
        owner.setId(1L);
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // When & Then
        assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(1L, true, 1L));
    }

    @Test
    void getBookingById_shouldThrowExceptionWhenBookingNotFound() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L));
    }

    @Test
    void getBookingById_shouldThrowExceptionWhenNotBookerOrOwner() {
        // Given
        Booking booking = new Booking();

        User booker = new User();
        booker.setId(2L);
        booking.setBooker(booker);

        Item item = new Item();
        User owner = new User();
        owner.setId(3L);
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // When & Then - user 1 is neither booker (2) nor owner (3)
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L));
    }

    @Test
    void getUserBookings_shouldHandleAllStates() {
        // Given
        User user = new User();
        user.setId(1L);

        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User", "user@email.com"));

        // Test ALL state
        when(bookingRepository.findByBookerId(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));

        // When
        List<BookingDto> result = bookingService.getUserBookings(1L, "ALL", 0, 10);

        // Then
        assertNotNull(result);
    }

    @Test
    void getUserBookings_shouldHandleCurrentState() {
        // Given
        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findCurrentByBookerId(eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));

        // When
        List<BookingDto> result = bookingService.getUserBookings(1L, "CURRENT", 0, 10);

        // Then
        assertNotNull(result);
    }

    @Test
    void getUserBookings_shouldHandlePastState() {
        // Given
        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findPastByBookerId(eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));

        // When
        List<BookingDto> result = bookingService.getUserBookings(1L, "PAST", 0, 10);

        // Then
        assertNotNull(result);
    }

    @Test
    void getUserBookings_shouldHandleFutureState() {
        // Given
        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findFutureByBookerId(eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));

        // When
        List<BookingDto> result = bookingService.getUserBookings(1L, "FUTURE", 0, 10);

        // Then
        assertNotNull(result);
    }

    @Test
    void getUserBookings_shouldHandleWaitingState() {
        // Given
        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findByBookerIdAndStatus(eq(1L), eq(BookingStatus.WAITING), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));

        // When
        List<BookingDto> result = bookingService.getUserBookings(1L, "WAITING", 0, 10);

        // Then
        assertNotNull(result);
    }

    @Test
    void getUserBookings_shouldHandleRejectedState() {
        // Given
        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findByBookerIdAndStatus(eq(1L), eq(BookingStatus.REJECTED), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));

        // When
        List<BookingDto> result = bookingService.getUserBookings(1L, "REJECTED", 0, 10);

        // Then
        assertNotNull(result);
    }

    @Test
    void getUserBookings_shouldHandleUnknownState() {
        // Given
        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findByBookerId(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));

        // When
        List<BookingDto> result = bookingService.getUserBookings(1L, "UNKNOWN", 0, 10);

        // Then
        assertNotNull(result);
    }

    // Similar tests for getOwnerBookings with all states
    @Test
    void getOwnerBookings_shouldHandleAllStates() {
        // Given
        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findByItemOwnerId(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));

        // When
        List<BookingDto> result = bookingService.getOwnerBookings(1L, "ALL", 0, 10);

        // Then
        assertNotNull(result);
    }

    @Test
    void getOwnerBookings_shouldHandleCurrentState() {
        // Given
        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findCurrentByOwnerId(eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));

        // When
        List<BookingDto> result = bookingService.getOwnerBookings(1L, "CURRENT", 0, 10);

        // Then
        assertNotNull(result);
    }

    @Test
    void getOwnerBookings_shouldHandleRejectedState() {
        // Given
        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.findByOwnerIdAndStatus(eq(1L), eq(BookingStatus.REJECTED), any(Pageable.class)))
                .thenReturn(List.of(new Booking()));

        // When
        List<BookingDto> result = bookingService.getOwnerBookings(1L, "REJECTED", 0, 10);

        // Then
        assertNotNull(result);
    }
}