package ru.practicum.shareit.gateway.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.gateway.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRequestCreateDto {
        private String description;
    }
}