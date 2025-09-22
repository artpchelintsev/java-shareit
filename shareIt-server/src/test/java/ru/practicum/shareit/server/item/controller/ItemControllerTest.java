package ru.practicum.shareit.server.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.ShareItServerApp;
import ru.practicum.shareit.server.item.ItemController;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@Import(ShareItServerApp.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem_shouldCreateItemSuccessfully() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description", true, null, null, null, null);
        ItemDto createdItem = new ItemDto(1L, "Test Item", "Test Description", true, null, null, null, null);

        when(itemService.createItem(any(ItemDto.class), anyLong())).thenReturn(createdItem);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"));

        verify(itemService, times(1)).createItem(any(ItemDto.class), eq(1L));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Test Item", "Test Description", true, null, null, null, null);

        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"));

        verify(itemService, times(1)).getItemById(eq(1L), eq(1L));
    }

    @Test
    void getAllItemsByOwner_shouldReturnItems() throws Exception {
        ItemDto item1 = new ItemDto(1L, "Item 1", "Desc 1", true, null, null, null, null);
        ItemDto item2 = new ItemDto(2L, "Item 2", "Desc 2", true, null, null, null, null);

        when(itemService.getAllItemsByOwner(anyLong(), anyInt(), anyInt())).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(itemService, times(1)).getAllItemsByOwner(eq(1L), eq(0), eq(10));
    }

    @Test
    void searchItems_shouldReturnSearchResults() throws Exception {
        ItemDto item = new ItemDto(1L, "Drill", "Power tool", true, null, null, null, null);

        when(itemService.searchItems(anyString(), anyInt(), anyInt())).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));

        verify(itemService, times(1)).searchItems(eq("drill"), eq(0), eq(10));
    }

    @Test
    void addComment_shouldAddCommentSuccessfully() throws Exception {
        CommentDto commentDto = new CommentDto(null, "Great item!", null, null);
        CommentDto createdComment = new CommentDto(1L, "Great item!", "Test User", LocalDateTime.now());

        when(itemService.addComment(anyLong(), any(CommentDto.class), anyLong())).thenReturn(createdComment);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"));

        verify(itemService, times(1)).addComment(eq(1L), any(CommentDto.class), eq(1L));
    }

    @Test
    void updateItem_shouldUpdateItemSuccessfully() throws Exception {
        ItemDto updateDto = new ItemDto(null, "Updated Item", null, false, null, null, null, null);
        ItemDto updatedItem = new ItemDto(1L, "Updated Item", "Original Desc", false, null, null, null, null);

        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong())).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.available").value(false));

        verify(itemService, times(1)).updateItem(eq(1L), any(ItemDto.class), eq(1L));
    }
}