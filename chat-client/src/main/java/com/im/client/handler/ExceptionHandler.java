package com.im.client.handler;

import com.im.client.ClientCommand;
import com.im.client.config.BeanFactoryUtils;
import com.im.client.session.ClientSession;
import com.im.common.InvalidFrameException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@ChannelHandler.Sharable
@Service
public class ExceptionHandler extends ChannelHandlerAdapter {

    //避免循环依赖
    //@Resource
    //private ClientCommand command;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // ..

        if (cause instanceof InvalidFrameException) {
            log.error(cause.getMessage());
            ClientSession.getSession(ctx).close();
        } else {

            //捕捉异常信息
//             cause.printStackTrace();
            log.error(cause.getMessage());
            ctx.close();

            ClientCommand command = BeanFactoryUtils.getBean(ClientCommand.class);
            //开始重连
            command.setConnectFlag(false);
            command.startConnectServer();
        }
//        super.exceptionCaught(ctx,cause);

    }

    /**
     * 通道 Read 读取 Complete 完成
     * 做刷新操作 ctx.flush()
     */
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}