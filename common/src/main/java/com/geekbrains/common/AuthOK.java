package com.geekbrains.common;

public class AuthOK implements Message {
    private String username;
    private String login;

    public AuthOK(String username, String login) {
        this.username = username;
        this.login = login;
    }

    public String getUsername() {
        return username;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public TypeMessage getTypeMessage() {
        return TypeMessage.AUTH_OK;
    }
}
