package ru.practicum.shareit.server.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.CommentRepository;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.request.repository.ItemRequestRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplBranchTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem_shouldThrowExceptionWhenNameIsBlank() {
        // Given
        ItemDto itemDto = new ItemDto(null, " ", "Description", true, null, null, null, null);

        // When & Then
        assertThrows(ValidationException.class, () -> itemService.createItem(itemDto, 1L));
    }

    @Test
    void createItem_shouldThrowExceptionWhenDescriptionIsBlank() {
        // Given
        ItemDto itemDto = new ItemDto(null, "Name", " ", true, null, null, null, null);

        // When & Then
        assertThrows(ValidationException.class, () -> itemService.createItem(itemDto, 1L));
    }

    @Test
    void createItem_shouldThrowExceptionWhenAvailableIsNull() {
        // Given
        ItemDto itemDto = new ItemDto(null, "Name", "Description", null, null, null, null, null);

        // When & Then
        assertThrows(ValidationException.class, () -> itemService.createItem(itemDto, 1L));
    }


    @Test
    void updateItem_shouldThrowExceptionWhenItemNotFound() {
        // Given
        ItemDto itemDto = new ItemDto(null, "Updated Name", null, null, null, null, null, null);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, itemDto, 1L));
    }

    @Test
    void updateItem_shouldThrowExceptionWhenNotOwner() {
        // Given
        ItemDto itemDto = new ItemDto(null, "Updated Name", null, null, null, null, null, null);

        Item existingItem = new Item();
        User owner = new User();
        owner.setId(2L); // Different owner
        existingItem.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        // When & Then - user 1 tries to update item owned by user 2
        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, itemDto, 1L));
    }

    @Test
    void updateItem_shouldThrowExceptionWhenNameIsEmpty() {
        // Given
        ItemDto itemDto = new ItemDto(null, "", null, null, null, null, null, null);

        Item existingItem = new Item();
        User owner = new User();
        owner.setId(1L);
        existingItem.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        // When & Then
        assertThrows(ValidationException.class, () -> itemService.updateItem(1L, itemDto, 1L));
    }

    @Test
    void updateItem_shouldThrowExceptionWhenDescriptionIsEmpty() {
        // Given
        ItemDto itemDto = new ItemDto(null, null, "", null, null, null, null, null);

        Item existingItem = new Item();
        User owner = new User();
        owner.setId(1L);
        existingItem.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        // When & Then
        assertThrows(ValidationException.class, () -> itemService.updateItem(1L, itemDto, 1L));
    }

    @Test
    void getItemById_shouldThrowExceptionWhenItemNotFound() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L));
    }

    @Test
    void searchItems_shouldReturnEmptyListWhenTextIsNull() {
        // When
        List<ItemDto> result = itemService.searchItems(null, 0, 10);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_shouldReturnEmptyListWhenTextIsBlank() {
        // When
        List<ItemDto> result = itemService.searchItems("   ", 0, 10);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_shouldReturnItemsWhenTextIsProvided() {
        // Given
        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Power tool");
        item.setAvailable(true);

        when(itemRepository.searchAvailableItems(eq("drill"), any(PageRequest.class)))
                .thenReturn(List.of(item));

        // When
        List<ItemDto> result = itemService.searchItems("drill", 0, 10);

        // Then
        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());
    }

    @Test
    void addComment_shouldThrowExceptionWhenItemNotFound() {
        // Given
        CommentDto commentDto = new CommentDto(null, "Great item!", null, null);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, commentDto, 1L));
    }

    @Test
    void addComment_shouldThrowExceptionWhenCommentTextIsBlank() {
        // Given
        CommentDto commentDto = new CommentDto(null, "   ", null, null);

        Item item = new Item();
        item.setId(1L);


        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // When & Then
        assertThrows(ValidationException.class, () -> itemService.addComment(1L, commentDto, 1L));
    }

    @Test
    void addComment_shouldThrowExceptionWhenCommentTextIsNull() {
        CommentDto commentDto = new CommentDto(null, null, null, null);

        Item item = new Item();
        item.setId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, commentDto, 1L));
    }

    @Test
    void addComment_shouldThrowExceptionWhenNoApprovedBooking() {
        CommentDto commentDto = new CommentDto(null, "Great item!", null, null);

        Item item = new Item();
        item.setId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User", "user@email.com"));

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, commentDto, 1L));
    }


    @Test
    void getAllItemsByOwner_shouldHandleEmptyList() {
        // Given
        when(itemRepository.findByOwnerIdOrderById(eq(1L), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        // When
        List<ItemDto> result = itemService.getAllItemsByOwner(1L, 0, 10);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getItemById_shouldIncludeBookingsWhenUserIsOwner() {
        // Given
        Item item = new Item();
        item.setId(1L);
        User owner = new User();
        owner.setId(1L);
        item.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookings(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking()));
        when(bookingRepository.findNextBookings(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking()));
        when(commentRepository.findByItemId(1L)).thenReturn(List.of(new Comment()));

        // When
        ItemDto result = itemService.getItemById(1L, 1L); // User is owner

        // Then
        assertNotNull(result);
    }

    @Test
    void getItemById_shouldNotIncludeBookingsWhenUserIsNotOwner() {
        // Given
        Item item = new Item();
        item.setId(1L);
        User owner = new User();
        owner.setId(2L); // Different owner
        item.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L)).thenReturn(List.of(new Comment()));

        // When
        ItemDto result = itemService.getItemById(1L, 1L); // User is not owner

        // Then
        assertNotNull(result);
        // Bookings should not be set for non-owners
    }
}