package ru.yandex.practicum.filmorate.storage.db.dao;/* # parse("File Header.java")*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;


/**
 * File Name: UsersDao.java
 * Author: Marina Volkova
 * Date: 2023-05-26,   3:49 PM (UTC+3)
 * Description:
 */
@Component
public class UsersDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserValidator validator = new UserValidator();

    @Autowired
    public UsersDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUserById(Integer id) {

        String sql = "SELECT * FROM USERS WHERE user_id = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);

        if (rowSet.next()) {
            User user = new User(rowSet.getString("email"),
                    rowSet.getString("login"),
                    rowSet.getDate("birthday").toLocalDate());
            user.setName(rowSet.getString("name"));
            user.setId(id);
            return user;
        } else {
            throw new NotFoundException("Отсутствуют данные в БД по указанному ID.");
        }
    }

    public void deleteUserById(Integer id) {
        String sql = "DELETE FROM USERS WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public User save(User user) {
        validator.isValid(user);

        String insertSql = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
        String selectSql = "SELECT USER_ID FROM USERS WHERE EMAIL = ?";

        jdbcTemplate.update(insertSql, user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());

        SqlRowSet rs = jdbcTemplate.queryForRowSet(selectSql, user.getEmail());

        int id = 0;

        if (rs.next()) {
            id = rs.getInt("user_id");
        }
        user.setId(id);
        return user;
    }

    public User update(User user) {
        validator.isValid(user);
        return user;
    }

}
