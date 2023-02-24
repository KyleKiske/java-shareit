package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.NotCurrentBooking;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String description;

    Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    User owner;

    @Transient
    private NotCurrentBooking lastBooking;

    @Transient
    private NotCurrentBooking nextBooking;

    @Transient
    private List<CommentDto> comments;
}
