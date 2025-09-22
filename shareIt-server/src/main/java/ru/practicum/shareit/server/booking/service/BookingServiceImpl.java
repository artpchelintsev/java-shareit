package ru.practicum.shareit.server.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.BookingStatus;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.mapper.BookingMapper;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.exception.ForbiddenException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto.BookingRequest bookingRequest, Long userId) {
        User booker = getUser(userId);
        Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner cannot book their own item");
        }

        validateBookingDates(bookingRequest.getStart(), bookingRequest.getEnd());

        Booking booking = new Booking();
        booking.setStart(bookingRequest.getStart());
        booking.setEnd(bookingRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only owner can approve booking");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking is already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Access denied");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(Long userId, String state, int from, int size) {
        getUser(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findCurrentByBookerId(userId, now, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findPastByBookerId(userId, now, pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureByBookerId(userId, now, pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookings = bookingRepository.findByBookerId(userId, pageable);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getOwnerBookings(Long userId, String state, int from, int size) {
        getUser(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findCurrentByOwnerId(userId, now, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findPastByOwnerId(userId, now, pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureByOwnerId(userId, now, pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerId(userId, pageable);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return UserMapper.toUser(userService.getUserById(userId));
    }

    private void validateBookingDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ValidationException("Start and end dates are required");
        }
        if (start.isAfter(end)) {
            throw new ValidationException("Start date cannot be after end date");
        }
        if (start.isEqual(end)) {
            throw new ValidationException("Start and end dates cannot be equal");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date cannot be in the past");
        }
    }
}