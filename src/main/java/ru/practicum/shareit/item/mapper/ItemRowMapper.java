package ru.practicum.shareit.item.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ItemRowMapper implements RowMapper<Item> {
    @Override
    public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setName(rs.getString("name"));
        item.setDescription(rs.getString("description"));
        item.setAvailable(rs.getBoolean("available"));
        item.setOwnerId(rs.getInt("ownerId"));
        return item;
    }
}
