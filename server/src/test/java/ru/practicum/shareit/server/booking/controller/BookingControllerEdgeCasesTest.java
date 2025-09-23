package ru.practicum.shareit.server.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.booking.BookingController;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerEdgeCasesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void getUserBookings_shouldReturnEmptyList() throws Exception {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBookingById_shouldReturn404WhenNotFound() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Booking not found"));

        mockMvc.perform(get("/bookings/999")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found"));
    }

    @Test
    void createBooking_shouldReturn400WhenInvalidDates() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingDto.BookingRequest bookingRequest = new BookingDto.BookingRequest(1L, start, end);

        when(bookingService.createBooking(any(), anyLong()))
                .thenThrow(new ValidationException("Invalid dates"));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid dates"));
    }

    @Test
    void approveBooking_shouldReturn404WhenBookingNotFound() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenThrow(new NotFoundException("Booking not found"));

        mockMvc.perform(patch("/bookings/999")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found"));
    }

    @Test
    void approveBooking_shouldReturn400WhenAlreadyProcessed() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenThrow(new ValidationException("Booking already processed"));

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Booking already processed"));
    }
}