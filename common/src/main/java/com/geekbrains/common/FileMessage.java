package com.geekbrains.common;

public class FileMessage implements Message {
    private String login;
    private String fileName;
    private String dirDestination;
    private byte[] arrayInfoBytes;
    private int numberPackage;
    private int countPackage;

    public FileMessage(String login, String fileName, String dirDestination, byte[] arrayInfoBytes,
                       int numberPackage, int countPackage) {
        this.login = login;
        this.fileName = fileName;
        this.dirDestination = dirDestination;
        this.arrayInfoBytes = arrayInfoBytes;
        this.numberPackage = numberPackage;
        this.countPackage = countPackage;
    }

    public String getLogin() {
        return login;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDirDestination() {
        return dirDestination;
    }

    public byte[] getArrayInfoBytes() {
        return arrayInfoBytes;
    }

    public int getNumberPackage() {
        return numberPackage;
    }

    public int getCountPackage() {
        return countPackage;
    }

    @Override
    public TypeMessage getTypeMessage() {
        return TypeMessage.FILE_MESSAGE;
    }
}
