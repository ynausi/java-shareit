package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "users",schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "email",nullable = false,unique = true)
    private String email;
    @Column(name = "name",nullable = false)
    private String name;
    @OneToMany(mappedBy = "user")
    private List<Item> items = new ArrayList<>();
}
