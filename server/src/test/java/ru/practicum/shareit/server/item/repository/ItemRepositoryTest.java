package ru.practicum.shareit.server.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByOwnerIdOrderById_shouldReturnItemsForOwner() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        entityManager.persist(owner);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(owner);
        entityManager.persist(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        entityManager.persist(item2);

        entityManager.flush();

        var items = itemRepository.findByOwnerIdOrderById(owner.getId(), Pageable.ofSize(10));
        assertEquals(2, items.size());
        assertEquals("Item 1", items.get(0).getName());
        assertEquals("Item 2", items.get(1).getName());
    }

    @Test
    void searchAvailableItems_shouldReturnMatchingItems() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        entityManager.persist(owner);

        Item item = new Item();
        item.setName("Power Drill");
        item.setDescription("Electric tool for drilling");
        item.setAvailable(true);
        item.setOwner(owner);
        entityManager.persist(item);
        entityManager.flush();

        var items = itemRepository.searchAvailableItems("drill", Pageable.ofSize(10));
        assertEquals(1, items.size());
        assertEquals("Power Drill", items.get(0).getName());
    }

    @Test
    void searchAvailableItems_shouldNotReturnUnavailableItems() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        entityManager.persist(owner);

        Item item = new Item();
        item.setName("Power Drill");
        item.setDescription("Electric tool for drilling");
        item.setAvailable(false); // Not available
        item.setOwner(owner);
        entityManager.persist(item);
        entityManager.flush();

        var items = itemRepository.searchAvailableItems("drill", Pageable.ofSize(10));
        assertTrue(items.isEmpty());
    }
}
