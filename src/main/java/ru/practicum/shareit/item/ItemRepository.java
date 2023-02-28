package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT I FROM Item AS I " +
            "WHERE upper(I.name) LIKE upper(concat('%', ?1, '%')) " +
            "OR upper(I.description) LIKE upper(concat('%', ?1, '%')) " +
            "AND I.available IS true")
    List<Item> findAllByText(String text, Pageable pageable);

    List<Item> findAllByOwnerId(long ownerId, Pageable pageable);

    List<Item> findAllByRequestId(long requestId);
}
