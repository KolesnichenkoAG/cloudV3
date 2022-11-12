package com.geekbrains.server;

import com.geekbrains.util.FilesUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import sun.plugin2.message.Message;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<Message> {
    private BDAuthenticationHandler authenticationHandler;
    private FilesUtils filesUtils;
    public MessageHandler(BDAuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        log.debug("Client connected...");
        filesUtils = new FilesUtils();
    }
}
