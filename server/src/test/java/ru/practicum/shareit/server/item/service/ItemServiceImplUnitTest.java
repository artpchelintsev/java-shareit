package ru.practicum.shareit.server.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.CommentRepository;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.request.repository.ItemRequestRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

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

    private User owner;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@email.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);

        itemDto = new ItemDto(null, "Test Item", "Test Description", true, null, null, null, null);
    }

    @Test
    void createItem_shouldThrowValidationExceptionWhenNameIsNull() {
        // Given
        ItemDto invalidDto = new ItemDto(null, null, "Description", true, null, null, null, null);

        // When & Then
        assertThrows(ValidationException.class,
                () -> itemService.createItem(invalidDto, 1L));
    }

    @Test
    void createItem_shouldThrowValidationExceptionWhenNameIsBlank() {
        // Given
        ItemDto invalidDto = new ItemDto(null, "", "Description", true, null, null, null, null);

        // When & Then
        assertThrows(ValidationException.class,
                () -> itemService.createItem(invalidDto, 1L));
    }

    @Test
    void createItem_shouldThrowValidationExceptionWhenDescriptionIsNull() {
        // Given
        ItemDto invalidDto = new ItemDto(null, "Name", null, true, null, null, null, null);

        // When & Then
        assertThrows(ValidationException.class,
                () -> itemService.createItem(invalidDto, 1L));
    }

    @Test
    void createItem_shouldThrowValidationExceptionWhenAvailableIsNull() {
        // Given
        ItemDto invalidDto = new ItemDto(null, "Name", "Description", null, null, null, null, null);

        // When & Then
        assertThrows(ValidationException.class,
                () -> itemService.createItem(invalidDto, 1L));
    }

    @Test
    void updateItem_shouldThrowNotFoundWhenItemNotFound() {
        // Given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, itemDto, 1L));
    }

    @Test
    void updateItem_shouldThrowNotFoundWhenNotOwner() {
        // Given
        User otherUser = new User(2L, "Other", "other@email.com");
        item.setOwner(otherUser);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // When & Then
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, itemDto, 1L));
    }

    @Test
    void updateItem_shouldThrowValidationExceptionWhenNameIsBlank() {
        // Given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        ItemDto updateDto = new ItemDto(null, "", null, null, null, null, null, null);

        // When & Then
        assertThrows(ValidationException.class,
                () -> itemService.updateItem(1L, updateDto, 1L));
    }

    @Test
    void updateItem_shouldThrowValidationExceptionWhenDescriptionIsBlank() {
        // Given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        ItemDto updateDto = new ItemDto(null, null, "", null, null, null, null, null);

        // When & Then
        assertThrows(ValidationException.class,
                () -> itemService.updateItem(1L, updateDto, 1L));
    }

    @Test
    void getItemById_shouldThrowNotFoundWhenItemNotFound() {
        // Given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(1L, 1L));
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
        List<ItemDto> result = itemService.searchItems("", 0, 10);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_shouldThrowNotFoundWhenItemNotFound() {
        // Given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class,
                () -> itemService.addComment(1L, new CommentDto(null, "Comment", null, null), 1L));
    }

    @Test
    void addComment_shouldThrowValidationExceptionWhenNoApprovedBooking() {
        // Given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userService.getUserById(anyLong()))
                .thenReturn(new UserDto(1L, "User", "user@email.com"));


        // When & Then
        assertThrows(ValidationException.class,
                () -> itemService.addComment(1L, new CommentDto(null, "Comment", null, null), 1L));
    }


    @Test
    void addComment_shouldThrowValidationExceptionWhenTextIsNull() {
        // Given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));


        // When & Then
        assertThrows(ValidationException.class,
                () -> itemService.addComment(1L, new CommentDto(null, null, null, null), 1L));
    }

    @Test
    void addComment_shouldThrowValidationExceptionWhenTextIsBlank() {
        // Given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));


        // When & Then
        assertThrows(ValidationException.class,
                () -> itemService.addComment(1L, new CommentDto(null, "   ", null, null), 1L));
    }

}