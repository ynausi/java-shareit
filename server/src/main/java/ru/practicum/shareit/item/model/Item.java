package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items",schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name",length = 50,nullable = false)
    private String name;
    @Column(name = "description",length = 200,nullable = false)
    private String description;
    @Column(name = "available",nullable = false)
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @OneToMany(mappedBy = "item")
    private List<Comment> comments = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;
}
