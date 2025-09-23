package ru.practicum.shareit.server.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.CommentRepository;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.repository.ItemRequestRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplAdditionalTest {

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
    void createItem_shouldAttachRequest_whenRequestExists() {
        itemDto.setRequestId(100L);
        ItemRequest request = new ItemRequest();
        request.setId(100L);
        when(userService.getUserById(anyLong())).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(itemRequestRepository.findById(100L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemDto result = itemService.createItem(itemDto, 1L);

        assertNotNull(result);
        assertEquals("Test Item", result.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_shouldThrowNotFound_whenRequestDoesNotExist() {
        itemDto.setRequestId(100L);
        when(userService.getUserById(anyLong())).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(itemRequestRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 1L));
    }

    @Test
    void addComment_shouldSaveComment_whenBookingExists() {
        CommentDto commentDto = new CommentDto(null, "Nice!", null, null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "User", "user@email.com"));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

        CommentDto result = itemService.addComment(1L, commentDto, 1L);

        assertNotNull(result);
        assertEquals("Nice!", result.getText());
    }

    @Test
    void getItemById_shouldHandleEmptyBookingsAndComments() {
        item.setOwner(owner);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookings(anyLong(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findNextBookings(anyLong(), any())).thenReturn(Collections.emptyList());
        when(commentRepository.findByItemId(1L)).thenReturn(null);

        ItemDto result = itemService.getItemById(1L, owner.getId());

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertNull(result.getComments());
    }

    @Test
    void getAllItemsByOwner_shouldHandleEmptyBookingsAndComments() {
        when(itemRepository.findByOwnerIdOrderById(eq(1L), any(PageRequest.class)))
                .thenReturn(List.of(item));
        when(bookingRepository.findLastBookings(anyLong(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findNextBookings(anyLong(), any())).thenReturn(Collections.emptyList());
        when(commentRepository.findByItemId(anyLong())).thenReturn(Collections.emptyList());

        List<ItemDto> results = itemService.getAllItemsByOwner(1L, 0, 10);

        assertEquals(1, results.size());
        assertNotNull(results.get(0));
        assertTrue(results.get(0).getComments().isEmpty());
        assertNull(results.get(0).getLastBooking());
        assertNull(results.get(0).getNextBooking());
    }

    @Test
    void updateItem_shouldUpdateAvailable() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        ItemDto updateDto = new ItemDto(null, null, null, false, null, null, null, null);
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemDto result = itemService.updateItem(1L, updateDto, 1L);

        assertNotNull(result);
        assertFalse(result.getAvailable());
    }
}
