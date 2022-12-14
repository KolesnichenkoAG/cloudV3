package com.geekbrains.server;

public interface AuthenticationProvider {

    String getUsernameByLogin(String login);

    String getPasswordByLogin(String login);

    boolean isLoginUsed(String login);

    boolean isEmailUsed(String login);

    String getUuidByLogin(String login);

    boolean newUser(String lastname, String name, String email, String login, String password, String uuid);

    void connectBD();

    void disconnectBD();
}
