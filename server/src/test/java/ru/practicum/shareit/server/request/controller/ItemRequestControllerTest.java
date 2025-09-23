package ru.practicum.shareit.server.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.request.ItemRequestController;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createItemRequest_shouldCreateRequest() throws Exception {
        // Given
        ItemRequestDto.ItemRequestCreateDto requestDto =
                new ItemRequestDto.ItemRequestCreateDto("Need a drill");

        ItemRequestDto createdRequest = new ItemRequestDto(1L, "Need a drill",
                LocalDateTime.now(), List.of());

        when(itemRequestService.createItemRequest(any(ItemRequestDto.ItemRequestCreateDto.class), anyLong()))
                .thenReturn(createdRequest);

        // When & Then
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));

        verify(itemRequestService, times(1))
                .createItemRequest(any(ItemRequestDto.ItemRequestCreateDto.class), eq(1L));
    }

    @Test
    void getUserItemRequests_shouldReturnRequests() throws Exception {
        // Given
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Need a drill",
                LocalDateTime.now(), List.of());

        when(itemRequestService.getUserItemRequests(anyLong()))
                .thenReturn(List.of(requestDto));

        // When & Then
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(itemRequestService, times(1)).getUserItemRequests(eq(1L));
    }

    @Test
    void getOtherUsersItemRequests_shouldReturnRequests() throws Exception {
        // Given
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Need a drill",
                LocalDateTime.now(), List.of());

        when(itemRequestService.getOtherUsersItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto));

        // When & Then
        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(itemRequestService, times(1)).getOtherUsersItemRequests(eq(1L), eq(0), eq(10));
    }

    @Test
    void getItemRequestById_shouldReturnRequest() throws Exception {
        // Given
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Need a drill",
                LocalDateTime.now(), List.of());

        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(requestDto);

        // When & Then
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));

        verify(itemRequestService, times(1)).getItemRequestById(eq(1L), eq(1L));
    }
}