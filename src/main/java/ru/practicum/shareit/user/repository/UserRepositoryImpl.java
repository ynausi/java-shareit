package ru.practicum.shareit.user.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.exceptions.UserEmailAlreadyExistsException;
import ru.practicum.shareit.user.mapper.UserRowMapper;
import ru.practicum.shareit.user.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private static final String FIND_ALL = "SELECT * FROM Users";
    private static final String FIND_BY_ID = "SELECT * FROM Users WHERE id = ?";
    private static final String ADD_TO_DB = "INSERT INTO Users(name,email) " +
            "VALUES (?,?)";
    private static final String UPDATE_USER = "UPDATE Users SET name = ?, email = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM Users WHERE id = ?";
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    @Override
    public Collection<User> findAll() {
        return jdbc.query(FIND_ALL,mapper);
    }

    @Override
    public Optional<User> findById(int id) {
        try {
            return Optional.of(jdbc.queryForObject(FIND_BY_ID,mapper,id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User save(User user) {
        try {
            int id = insertAndReturnId(ADD_TO_DB,
                    user.getName(),
                    user.getEmail()
            );
            user.setId(id);
            return user;
        } catch (DuplicateKeyException e) {
            throw new UserEmailAlreadyExistsException(user.getEmail());
        }
    }

    @Override
    public User update(User user) {
        jdbc.update(UPDATE_USER,
                user.getName(),
                user.getEmail(),
                user.getId()
        );
        return user;
    }

    @Override
    public void delete(int id) {
        jdbc.update(DELETE_USER,id);
    }

    protected int insertAndReturnId(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
