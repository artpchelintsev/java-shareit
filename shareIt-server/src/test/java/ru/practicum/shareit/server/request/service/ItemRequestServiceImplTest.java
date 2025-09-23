package ru.practicum.shareit.server.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.repository.ItemRequestRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createItemRequest_shouldThrowExceptionWhenDescriptionIsBlank() {
        // Given
        ItemRequestDto.ItemRequestCreateDto requestDto =
                new ItemRequestDto.ItemRequestCreateDto("   ");
        when(userService.getUserById(1L)).thenReturn(new UserDto());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.createItemRequest(requestDto, 1L));
    }

    @Test
    void createItemRequest_shouldThrowExceptionWhenDescriptionIsNull() {
        // Given
        ItemRequestDto.ItemRequestCreateDto requestDto =
                new ItemRequestDto.ItemRequestCreateDto(null);
        when(userService.getUserById(1L)).thenReturn(new UserDto());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> itemRequestService.createItemRequest(requestDto, 1L));
    }

    @Test
    void getItemRequestById_shouldThrowExceptionWhenRequestNotFound() {
        // Given
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(1L, 1L));
    }

    @Test
    void getItemRequestById_shouldReturnRequestWithItems() {
        // Given
        User user = new User();
        user.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Need item");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));

        // When
        ItemRequestDto result = itemRequestService.getItemRequestById(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Need item", result.getDescription());
    }

    @Test
    void getUserItemRequests_shouldReturnEmptyList() {
        // Given
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L))
                .thenReturn(List.of());

        // When
        List<ItemRequestDto> result = itemRequestService.getUserItemRequests(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOtherUsersItemRequests_shouldReturnEmptyList() {
        // Given
        when(userService.getUserById(1L)).thenReturn(new UserDto());
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(eq(1L), any()))
                .thenReturn(List.of());

        // When
        List<ItemRequestDto> result = itemRequestService.getOtherUsersItemRequests(1L, 0, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}