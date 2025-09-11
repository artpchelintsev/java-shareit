package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        validateItemDto(itemDto);
        getUser(ownerId);

        Item item = ItemMapper.toItem(itemDto);
        User owner = new User();
        owner.setId(ownerId);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Only owner can update item");
        }

        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new ValidationException("Item name cannot be empty");
            }
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new ValidationException("Item description cannot be empty");
            }
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        Hibernate.initialize(item.getOwner());

        ItemDto itemDto = ItemMapper.toItemDto(item);

        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            List<Booking> lastBookings = bookingRepository.findLastBookings(itemId, now);
            if (lastBookings != null && !lastBookings.isEmpty()) {
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBookings.get(0)));
            }

            List<Booking> nextBookings = bookingRepository.findNextBookings(itemId, now);
            if (nextBookings != null && !nextBookings.isEmpty()) {
                itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBookings.get(0)));
            }
        }

        List<Comment> comments = commentRepository.findByItemId(itemId);
        if (comments != null) {
            itemDto.setComments(comments.stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));
        }

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsByOwner(Long ownerId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.findByOwnerIdOrderById(ownerId, pageable);
        LocalDateTime now = LocalDateTime.now();

        return items.stream().map(item -> {
            ItemDto itemDto = ItemMapper.toItemDto(item);

            List<Booking> lastBookings = bookingRepository.findLastBookings(item.getId(), now);
            if (!lastBookings.isEmpty()) {
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBookings.get(0)));
            }

            List<Booking> nextBookings = bookingRepository.findNextBookings(item.getId(), now);
            if (!nextBookings.isEmpty()) {
                itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBookings.get(0)));
            }

            List<Comment> comments = commentRepository.findByItemId(item.getId());
            itemDto.setComments(comments.stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));

            return itemDto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        Pageable pageable = PageRequest.of(from / size, size);
        return itemRepository.searchAvailableItems(text, pageable).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        User author = getUser(userId);

        boolean hasApprovedBooking = bookingRepository.existsApprovedPastBooking(
                itemId, userId, LocalDateTime.now());

        if (!hasApprovedBooking) {
            throw new ValidationException("User must have approved booking for this item to leave a comment");
        }

        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Comment text cannot be empty");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(savedComment);
    }

    private User getUser(Long userId) {
        return UserMapper.toUser(userService.getUserById(userId));
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Item name cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Item description cannot be empty");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Item available status cannot be null");
        }
    }
}