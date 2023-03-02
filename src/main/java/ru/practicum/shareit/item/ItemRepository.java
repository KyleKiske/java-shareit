package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT I FROM Item AS I " +
            "WHERE upper(I.name) LIKE upper(concat('%', :text, '%')) " +
            "OR upper(I.description) LIKE upper(concat('%', :text, '%')) " +
            "AND I.available IS true")
    Page<Item> findAllByText(@Param("text") String text, Pageable pageable);

    Page<Item> findAllByOwnerId(long ownerId, Pageable pageable);

    List<Item> findAllByRequestId(long requestId);

    List<Item> findAllByRequestIdIsIn(List<Long> requestList);
}
