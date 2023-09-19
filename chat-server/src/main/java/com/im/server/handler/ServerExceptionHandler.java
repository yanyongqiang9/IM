package com.im.server.handler;

import com.im.common.InvalidFrameException;
import com.im.server.session.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@ChannelHandler.Sharable
@Service
public class ServerExceptionHandler extends ChannelHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // ..

        if (cause instanceof InvalidFrameException) {
            cause.printStackTrace();
            log.error(cause.getMessage());
//            ServerSession.closeSession(ctx);
        }
        if (cause instanceof IOException) {
          //远程连接，已经关闭
            log.error(cause.getMessage());
            log.error("客户端已经关闭连接，这里需要做下线处理");
            ServerSession.closeSession(ctx);
        } else {
            cause.printStackTrace();

            //捕捉异常信息
//            cause.printStackTrace();
            log.error(cause.getMessage());
//            ctx.close();
        }
    }

    /**
     * 通道 Read 读取 Complete 完成
     * 做刷新操作 ctx.flush()
     */
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ServerSession.closeSession(ctx);
    }


}