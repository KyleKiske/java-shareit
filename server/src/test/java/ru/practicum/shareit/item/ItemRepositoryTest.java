package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.TestObjectMaker;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByText() {
        EntityManager entityManager = testEntityManager.getEntityManager();
        TypedQuery<Item> query = entityManager.createQuery(
                "SELECT I FROM Item AS I " +
                        "WHERE upper(I.name) LIKE upper(concat('%', ?1, '%')) " +
                        "OR upper(I.description) LIKE upper(concat('%', ?1, '%')) " +
                        "AND I.available IS true", Item.class
        );
        User user = TestObjectMaker.makeUser(1);
        userRepository.save(user);

        Item item = new Item(1L, "first", "brand new", true, user, null, null, null, null);
        Item itemTwo = new Item(2L, "second", "kinda new", true, user, null, null, null, null);

        itemRepository.save(item);
        itemRepository.save(itemTwo);

        assertEquals(query.setParameter(1, "first").getResultList().size(), 1);
        assertEquals(query.setParameter(1, "new").getResultList().size(), 2);
        assertEquals(query.setParameter(1, "third").getResultList().size(), 0);
    }

}