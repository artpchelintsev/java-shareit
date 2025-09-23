package ru.practicum.shareit.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "Drill", "Power tool", true, null, null, null, null);
        commentDto = new CommentDto(1L, "Great item!", "User", LocalDateTime.now());
    }

    @Test
    void createItem_shouldReturnItem() throws Exception {
        when(itemService.createItem(any(ItemDto.class), eq(1L))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));

        verify(itemService, times(1)).createItem(any(ItemDto.class), eq(1L));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        ItemDto updatedDto = new ItemDto(1L, "Updated Drill", "Power tool", true, null, null, null, null);
        when(itemService.updateItem(eq(1L), any(ItemDto.class), eq(1L))).thenReturn(updatedDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Drill"));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        when(itemService.getItemById(1L, 1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
    }

    @Test
    void getAllItemsByOwner_shouldReturnList() throws Exception {
        when(itemService.getAllItemsByOwner(1L, 0, 10)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()));
    }

    @Test
    void searchItems_shouldReturnList() throws Exception {
        when(itemService.searchItems("drill", 0, 10)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {
        when(itemService.addComment(eq(1L), any(CommentDto.class), eq(1L))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }

    @Test
    void getItemById_shouldReturn404WhenNotFound() throws Exception {
        when(itemService.getItemById(1L, 1L)).thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}
