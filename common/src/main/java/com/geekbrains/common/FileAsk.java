package com.geekbrains.common;

public class FileAsk implements Message {
    private String fileName;
    private String login;
    private String dirDestination;

    public FileAsk(String fileName, String dirDestination, String login) {
        this.fileName = fileName;
        this.dirDestination = dirDestination;
        this.login = login;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLogin() {
        return login;
    }

    public String getDirDestination() {
        return dirDestination;
    }

    @Override
    public TypeMessage getTypeMessage() {
        return TypeMessage.FILE_ASK;
    }
}
