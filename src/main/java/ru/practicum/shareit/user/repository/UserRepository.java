package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    //Collection<User> findAll();

    Optional<User> findById(int userId);

    void deleteById(int id);

}
