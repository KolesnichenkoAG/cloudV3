package com.geekbrains.server;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class BDAuthenticationProvider implements AuthenticationProvider {
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;

    static final String USER = "postgres";
    static final String PASS = "postgres";

    @Override
    public String getUsernameByLogin(String login) {
        try {
            ps = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
            ps.setString(1, login);

            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getString(2) + " " + rs.getString(3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Ошибка при работе с таблицей users в БД");
            return null;
        }
        return null;
    }

    @Override
    public String getPasswordByLogin(String login) {
        try {
            ps = connection.prepareStatement("SELECT * FROM users Where login = ?");
            ps.setString(1, login);
            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getString(6);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Ошибка при работе с таблицей users в БД");
            return null;
        }
        return null;
    }


    @Override
    public boolean isLoginUsed(String login) {
        try {
            ps = connection.prepareStatement("SELECT * FROM users Where login = ?");
            ps.setString(1, login);
            rs = ps.executeQuery();
            while (rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Ошибка при работе с таблицей users в БД. Введенный login уже существует");
            return true;
        }
    }

    @Override
    public boolean isEmailUsed(String email) {
        try {
            ps = connection.prepareStatement("SELECT * FROM users Where email = ?");
            ps.setString(1, email);
            rs = ps.executeQuery();
            while (rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Ошибка при работе с таблицей users в БД. Введенный email уже существует");
            return true;
        }
    }

    @Override
    public String getUuidByLogin(String login) {
        try {
            ps = connection.prepareStatement("SELECT * FROM users Where login = ?");
            ps.setString(1, login);
            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getString(7);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Ошибка при работе с таблицей users в БД. Поиск папки UUID не удался");
            return null;
        }
        return null;
    }

    @Override
    public String getLoginByEmail(String email) {
        try {
            ps = connection.prepareStatement("SELECT * FROM users Where email = ?");
            ps.setString(1, email);
            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getString(5);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Ошибка при работе с таблицей users в БД");
            return null;
        }
        return null;
    }

    @Override
    public boolean newUser(String lastname, String name, String email, String login, String password, String uuid) {
        try {
            ps = connection.prepareStatement("INSERT INTO users (lastname, name, email, login, password, uuid) " +
                    " VALUES(?,?,?,?,?,?)");
            ps.setString(1, lastname);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, login);
            ps.setString(5, password);
            ps.setString(6, uuid);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Ошибка при работе с таблицей users в БД. Создание нового пользователя не удалось");
            return false;
        }
    }

    @Override
    public void connectBD() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            connection = DriverManager.getConnection("название таблицы", USER, PASS); // сюда вставить название таблоицы которую подключим
            log.debug("Соединение с базой данных установленно");
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Ошибка соединения с БД");
        }
    }

    @Override
    public void disconnectBD() {
        log.debug("Соединение с БД разорвано");
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
