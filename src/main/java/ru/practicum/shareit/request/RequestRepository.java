package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterIdOrderByCreatedDesc(long requesterId);

    Page<Request> findAllByRequesterIdIsNotOrderByCreatedDesc(long requesterId, Pageable pageable);
}
