package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.InternalServerException;
import ru.practicum.shareit.item.mapper.ItemRowMapper;
import ru.practicum.shareit.item.model.Item;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final JdbcTemplate jdbc;
    private final ItemRowMapper mapper;
    private static final String  FIND_ALL = "SELECT * FROM Items";
    private static final String FIND_BY_ID = "SELECT * FROM Items WHERE id = ?";
    private static final String ADD_TO_DB = "INSERT INTO Items " +
            "(name,description,available,ownerId) " +
            "VALUES (?,?,?,?)";
    private static final  String UPDATE_ITEM = "UPDATE Items SET " +
            "name = ? , description = ? , available = ? , ownerId = ? " +
            "WHERE id = ?";
    private static final  String DELETE_ITEM = "DELETE FROM Items WHERE id = ?";

    private static final String SEARCH_ITEMS_BY_NAME = "SELECT * FROM Items WHERE available = true AND " +
            "LOWER(name) LIKE(CONCAT('%',LOWER(?),'%')) ";
    private static final  String SEARCH_USER_ITEMS = "SELECT * FROM Items WHERE ownerId = ?";

    @Override
    public Collection<Item> findAll() {
        return jdbc.query(FIND_ALL,mapper);
    }

    @Override
    public Collection<Item> searchUsersItems(int ownerId) {
        return jdbc.query(SEARCH_USER_ITEMS,mapper,ownerId);
    }

    @Override
    public Collection<Item> searchByName(String name) {
        return jdbc.query(SEARCH_ITEMS_BY_NAME,mapper,name);
    }

    @Override
    public Optional<Item> findById(int id) {
        try {
            return Optional.of(jdbc.queryForObject(FIND_BY_ID,mapper,id));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Item save(Item item,int ownerId) {
        int id = insertAndReturnId(ADD_TO_DB,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                ownerId
        );
        item.setId(id);
        item.setOwnerId(ownerId);
        return item;
    }

    @Override
    public Item update(Item item) {
       jdbc.update(UPDATE_ITEM,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getId()
       );
       return item;
    }


    @Override
    public void delete(int id) {
        jdbc.update(DELETE_ITEM,id);
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
