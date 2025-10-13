package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaMapper {

    public static Mpa mapToMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("mpa_id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}
