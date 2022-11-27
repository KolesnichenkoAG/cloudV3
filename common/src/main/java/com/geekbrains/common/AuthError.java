package com.geekbrains.common;

public class AuthError implements Message {

    @Override
    public TypeMessage getTypeMessage() {
        return TypeMessage.AUTH_ERROR;
    }
}
