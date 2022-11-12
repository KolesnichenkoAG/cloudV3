package com.geekbrains.server;

public class BDAuthenticationHandler implements AuthenticationProvider {



    @Override
    public String getUsernameByLogin(String login) {
        return null;
    }

    @Override
    public String getPasswordByLogin(String login) {
        return null;
    }

    @Override
    public boolean isLoginUsed(String login) {
        return false;
    }

    @Override
    public boolean isEmailUsed(String login) {
        return false;
    }

    @Override
    public String getUuidByLogin(String login) {
        return null;
    }

    @Override
    public String getLoginByEmail(String email) {
        return null;
    }

    @Override
    public boolean newUser(String lastname, String name, String email, String login, String password, String uuid) {
        return false;
    }

    @Override
    public void connectDB() {

    }

    @Override
    public void disconnectBD() {

    }
}
