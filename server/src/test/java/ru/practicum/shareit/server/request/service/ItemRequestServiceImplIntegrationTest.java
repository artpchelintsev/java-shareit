package ru.practicum.shareit.server.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.ShareItServerApp;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServerApp.class)
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    private Long userId;

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto(null, "Test User", "test@email.com");
        UserDto createdUser = userService.createUser(userDto);
        userId = createdUser.getId();
    }

    @Test
    void createItemRequest_shouldCreateRequestSuccessfully() {
        // Given
        ItemRequestDto.ItemRequestCreateDto requestDto =
                new ItemRequestDto.ItemRequestCreateDto("Need a drill");

        // When
        ItemRequestDto createdRequest = itemRequestService.createItemRequest(requestDto, userId);

        // Then
        assertNotNull(createdRequest.getId());
        assertEquals("Need a drill", createdRequest.getDescription());
        assertNotNull(createdRequest.getCreated());
    }

    @Test
    void getUserItemRequests_shouldReturnUserRequests() {
        // Given
        ItemRequestDto.ItemRequestCreateDto requestDto =
                new ItemRequestDto.ItemRequestCreateDto("Need a drill");
        itemRequestService.createItemRequest(requestDto, userId);

        // When
        List<ItemRequestDto> requests = itemRequestService.getUserItemRequests(userId);

        // Then
        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
    }

    @Test
    void getOtherUsersItemRequests_shouldReturnOtherUsersRequests() {
        // Given
        UserDto anotherUserDto = new UserDto(null, "Another User", "another@email.com");
        UserDto anotherUser = userService.createUser(anotherUserDto);

        ItemRequestDto.ItemRequestCreateDto requestDto =
                new ItemRequestDto.ItemRequestCreateDto("Need a drill");
        itemRequestService.createItemRequest(requestDto, anotherUser.getId());

        // When
        List<ItemRequestDto> requests = itemRequestService.getOtherUsersItemRequests(userId, 0, 10);

        // Then
        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
    }

    @Test
    void getItemRequestById_shouldReturnRequest() {
        // Given
        ItemRequestDto.ItemRequestCreateDto requestDto =
                new ItemRequestDto.ItemRequestCreateDto("Need a drill");
        ItemRequestDto createdRequest = itemRequestService.createItemRequest(requestDto, userId);

        // When
        ItemRequestDto foundRequest = itemRequestService.getItemRequestById(createdRequest.getId(), userId);

        // Then
        assertNotNull(foundRequest);
        assertEquals(createdRequest.getId(), foundRequest.getId());
        assertEquals("Need a drill", foundRequest.getDescription());
    }
}