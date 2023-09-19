package com.im.server.handler;


import com.im.common.bean.ProtoMsg;
import com.im.common.current.FutureTaskScheduler;
import com.im.server.processor.ChatRedirectProcessor;
import com.im.server.session.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
@ChannelHandler.Sharable
public class ChatRedirectHandler extends ChannelHandlerAdapter {

    @Resource
    ChatRedirectProcessor chatRedirectProcessor;

    /**
     * 收到消息
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        //判断消息实例
        if (!(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断消息类型
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = ((ProtoMsg.Message) msg).getType();
        if (!headType.equals(chatRedirectProcessor.type())) {
            super.channelRead(ctx, msg);
            return;
        }

        //反向导航
        ServerSession session = ctx.channel().attr(ServerSession.SESSION_KEY).get();

        //判断是否登录

        if (null == session || !session.isLogin()) {
            log.error("用户尚未登录，不能发送消息");
            return;
        }

        //异步处理IM消息转发的逻辑
        FutureTaskScheduler.add(() -> {
            chatRedirectProcessor.action(session, pkg);
        });


    }

}