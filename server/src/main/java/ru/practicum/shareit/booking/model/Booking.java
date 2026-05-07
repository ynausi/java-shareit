package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

@Entity
@Table(name = "Bookings",schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusValue status = StatusValue.WAITING;

    @ManyToOne
    @JoinColumn(name = "item_id",nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id",nullable = false)
    private User booker;

    @Column(name = "startTime",nullable = false)
    private LocalDateTime startTime;

    @Column(name = "endTime",nullable = false)
    private LocalDateTime endTime;


}
