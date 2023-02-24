package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT * FROM items AS I " +
            "WHERE upper(I.name) LIKE upper(concat('%', ?1, '%')) " +
            "OR upper(I.description) LIKE upper(concat('%', ?1, '%')) " +
            "AND I.available IS true",
    nativeQuery = true)
    List<Item> findAllByText(String text);

    List<Item> getAllByOwnerId(long owner);
}
