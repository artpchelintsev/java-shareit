package ru.practicum.shareit.server.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.ShareItServerApp;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServerApp.class)
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private Long ownerId;

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto(null, "Owner", "owner@email.com");
        UserDto createdUser = userService.createUser(userDto);
        ownerId = createdUser.getId();
    }

    @Test
    void createItem_shouldCreateItemSuccessfully() {
        // Given
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description", true, null, null, null, null);

        // When
        ItemDto createdItem = itemService.createItem(itemDto, ownerId);

        // Then
        assertNotNull(createdItem.getId());
        assertEquals("Test Item", createdItem.getName());
        assertEquals("Test Description", createdItem.getDescription());
        assertTrue(createdItem.getAvailable());
    }

    @Test
    void getAllItemsByOwner_shouldReturnOwnerItems() {
        // Given
        ItemDto item1 = new ItemDto(null, "Item 1", "Desc 1", true, null, null, null, null);
        ItemDto item2 = new ItemDto(null, "Item 2", "Desc 2", true, null, null, null, null);

        itemService.createItem(item1, ownerId);
        itemService.createItem(item2, ownerId);

        // When
        List<ItemDto> items = itemService.getAllItemsByOwner(ownerId, 0, 10);

        // Then
        assertEquals(2, items.size());
    }

    @Test
    void searchItems_shouldReturnMatchingItems() {
        // Given
        ItemDto item = new ItemDto(null, "Power Drill", "Electric tool", true, null, null, null, null);
        itemService.createItem(item, ownerId);

        // When
        List<ItemDto> results = itemService.searchItems("drill", 0, 10);

        // Then
        assertEquals(1, results.size());
        assertEquals("Power Drill", results.get(0).getName());
    }
}