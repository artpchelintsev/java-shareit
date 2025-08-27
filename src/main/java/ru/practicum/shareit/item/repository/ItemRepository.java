package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@org.springframework.stereotype.Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter.getAndIncrement());
        }
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Long itemId, Item item) {
        item.setId(itemId);
        items.put(itemId, item);
        return item;
    }

    public Item findById(Long itemId) {
        return items.get(itemId);
    }

    public List<Item> findAll() {
        return List.copyOf(items.values());
    }

    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null && ownerId.equals(item.getOwner().getId()))
                .collect(Collectors.toList());
    }
}
