package ru.practicum.shareit.server.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(
            Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' " +
            "AND b.end > :now ORDER BY b.start ASC")
    List<Booking> findNextBookings(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' " +
            "AND b.end < :now ORDER BY b.end DESC")
    List<Booking> findLastBookings(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :userId " +
            "AND b.status = 'APPROVED' AND b.end < :now")
    boolean existsApprovedPastBooking(@Param("itemId") Long itemId,
                                      @Param("userId") Long userId,
                                      @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.start < :now AND b.end > :now ORDER BY b.start DESC")
    List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId,
                                        @Param("now") LocalDateTime now,
                                        Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastByBookerId(@Param("bookerId") Long bookerId,
                                     @Param("now") LocalDateTime now,
                                     Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureByBookerId(@Param("bookerId") Long bookerId,
                                       @Param("now") LocalDateTime now,
                                       Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndStatus(@Param("bookerId") Long bookerId,
                                          @Param("status") BookingStatus status,
                                          Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.start < :now AND b.end > :now ORDER BY b.start DESC")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId,
                                       @Param("now") LocalDateTime now,
                                       Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId,
                                    @Param("now") LocalDateTime now,
                                    Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId,
                                      @Param("now") LocalDateTime now,
                                      Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                         @Param("status") BookingStatus status,
                                         Pageable pageable);
}
