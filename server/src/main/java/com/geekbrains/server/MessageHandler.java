package com.geekbrains.server;

import com.geekbrains.common.*;
import com.geekbrains.util.FilesUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<Message> {
    private BDAuthenticationProvider authenticationProvider;
    private FilesUtils filesUtils;

    public MessageHandler(BDAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client connected...");
        filesUtils = new FilesUtils();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client disconnected...");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        switch (msg.getTypeMessage()) {
            case AUTH_ASK:
                AuthAsk authAsk = (AuthAsk) msg;
                log.debug("Запрос клиента на авторизацию");
                String password = authenticationProvider.getPasswordByLogin(authAsk.getLogin());

                if (BCrypt.checkpw(authAsk.getPassword(), password)) {
                    String username = authenticationProvider.getUsernameByLogin(authAsk.getLogin());
                    if (username != null) {
                        ctx.writeAndFlush(new AuthOK(username, authAsk.getLogin()));
                        log.debug("Сообщение от сервера: авторизация пользователя " + authAsk.getLogin() + " успешна");
                    }
                } else {
                    ctx.writeAndFlush(new AuthError());
                    log.error("Сообщение от сервера: ошибка авторизации пользователя " + authAsk.getLogin());
                }
                break;

            case REG_ASK:
                RegAsk regAsk = (RegAsk) msg;
                if (authenticationProvider.isLoginUsed(regAsk.getLogin())) {
                    ctx.writeAndFlush(new RegError(regAsk.getLogin(),""));
                    break;
                }
                if (authenticationProvider.isEmailUsed(regAsk.getEmail())) {
                    ctx.writeAndFlush(new RegError("", regAsk.getEmail()));
                    break;
                }
                if (authenticationProvider.newUser(regAsk.getLastname(), regAsk.getName(), regAsk.getEmail(),
                        regAsk.getLogin(), regAsk.getPassword(), regAsk.getLogin())) {
                    ctx.writeAndFlush(new RegOK());
                }
                break;

            case LIST_ASK:
                ListAsk listAsk = (ListAsk) msg;
                log.debug("Запрос клиента о списке файлов");
                sendListFiles(ctx, listAsk.getLogin());
                break;

            case FILE_ASK:
                FileAsk fileAsk = (FileAsk) msg;
                log.debug("Запрос клиента о выгрузке файла с сервера");
                Path file = Paths.get(Paths.get("").toAbsolutePath().toString(), "server", "server",
                        fileAsk.getLogin()); // надо проверить название папки
                file = file.resolve(fileAsk.getFileName());
                if (file.toFile().exists()) {
                    filesUtils.sendFile(file.toString(), fileAsk.getLogin(), fileAsk.getDirDestination(), null, ctx);
                }
                break;

            case FILE_MESSAGE:
                FileMessage fileMessage = (FileMessage) msg;
                Path dirTmp = Paths.get(Paths.get("").toAbsolutePath().toString(), "server", "server",
                        "tmp", fileMessage.getLogin());
                Path dirDestination = Paths.get(Paths.get("").toAbsolutePath().toString(),
                        "server", "server", fileMessage.getLogin());
                if (filesUtils.saveFile(dirTmp, dirDestination, fileMessage)) {
                    sendListFiles(ctx, fileMessage.getLogin());
                }
                break;

            case DELETE_ASK:
                DeleteAsk deleteAsk = (DeleteAsk) msg;
                Path delFile = Paths.get(Paths.get("").toAbsolutePath().toString(), "server", "server",
                        deleteAsk.getLogin());
                delFile = delFile.resolve(deleteAsk.getFileName());
                filesUtils.deleteFile(delFile.toString());
                sendListFiles(ctx, deleteAsk.getLogin());
                break;

            case RENAME_ASK:
                RenameAsk renameAsk = (RenameAsk) msg;
                Path oldName = Paths.get(Paths.get("").toAbsolutePath().toString(), "server",
                        "server", renameAsk.getLogin());
                oldName = oldName.resolve(renameAsk.getOldName());
                Path newName = Paths.get(Paths.get("").toAbsolutePath().toString(), "server",
                        "server", renameAsk.getLogin());
                newName = newName.resolve(renameAsk.getNewName());
                filesUtils.renameFile(oldName.toString(), newName.toString());
                sendListFiles(ctx, renameAsk.getLogin());
                break;
        }
    }

    private List<String> getListFiles(String uuid) {
        Path path = Paths.get(Paths.get("").toAbsolutePath().toString(), "server", "server", uuid); // вроде проверить название папки
        filesUtils.createDirectory(path);

        List<String> listFile = new ArrayList<>();
        File dir = new File(path.toString());
        File[] arrFiles = dir.listFiles();
        for (File file : arrFiles) {
            listFile.add(file.getName());
        }
        return listFile;
    }

    private void sendListFiles(ChannelHandlerContext ctx, String login) {
        String uuid = authenticationProvider.getUuidByLogin(login);
        if (uuid != null) {
            ctx.writeAndFlush(new ListMessage(getListFiles(uuid)));
            log.debug("Сообщение от сервера: пользователю " + login + " отправлен список файлов");
        }
    }
}
