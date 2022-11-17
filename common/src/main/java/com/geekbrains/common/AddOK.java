package com.geekbrains.common;

public class AddOK implements Message {

    @Override
    public TypeMessage getTypeMessage() {
        return TypeMessage.ADD_OK;
    }
}
