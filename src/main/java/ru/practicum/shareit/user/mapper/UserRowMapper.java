package ru.practicum.shareit.user.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs,int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        return user;
    }
}
