package ru.practicum.shareit.server.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.ItemController;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerAdditionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        // Given
        ItemDto updateDto = new ItemDto(null, "Updated Item", null, false, null, null, null, null);
        ItemDto updatedItem = new ItemDto(1L, "Updated Item", "Original Desc", false, null, null, null, null);

        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong()))
                .thenReturn(updatedItem);

        // When & Then
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        // Given
        ItemDto itemDto = new ItemDto(1L, "Test Item", "Test Description", true, null, null, null, null);

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        // When & Then
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void getItemById_shouldReturn404WhenItemNotFound() throws Exception {
        // Given
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Item not found"));

        // When & Then
        mockMvc.perform(get("/items/999")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchItems_shouldReturnEmptyListWhenTextIsBlank() throws Exception {
        // Given
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/items/search")
                        .param("text", "   ")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}