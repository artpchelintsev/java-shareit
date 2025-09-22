package ru.practicum.shareit.server.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.repository.ItemRequestRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestDto.ItemRequestCreateDto itemRequestDto, Long userId) {
        User requestor = getUser(userId);

        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        getUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getOtherUsersItemRequests(Long userId, int from, int size) {
        getUser(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, pageable);
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        getUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findByIdWithItems(requestId)
                .orElseThrow(() -> new NotFoundException("Item request not found with id: " + requestId));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private User getUser(Long userId) {
        return UserMapper.toUser(userService.getUserById(userId));
    }
}