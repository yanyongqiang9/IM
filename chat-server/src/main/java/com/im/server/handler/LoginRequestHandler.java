package com.im.server.handler;

import com.im.common.bean.ProtoMsg;
import com.im.common.current.CallbackTask;
import com.im.common.current.CallbackTaskScheduler;
import com.im.server.processor.LoginProcessor;
import com.im.server.session.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service("LoginRequestHandler")
@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelHandlerAdapter {

    @Resource
    LoginProcessor loginProcessor;

    @Resource
    ChatRedirectHandler chatRedirectHandler;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        log.info("收到一个新的连接，但是没有登录 {}", ctx.channel().id());
        super.channelActive(ctx);
    }

    /**
     * 收到消息
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (!(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;

        //取得请求类型
        ProtoMsg.HeadType headType = pkg.getType();

        if (!headType.equals(loginProcessor.type())) {
            super.channelRead(ctx, msg);
            return;
        }

        ServerSession session = new ServerSession(ctx.channel());

        //异步任务，处理登录的逻辑
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                return loginProcessor.action(session, pkg);
            }

            //异步任务返回
            @Override
            public void onBack(Boolean r) {
                if (r) {

                    ctx.pipeline().addAfter("login", "chat", chatRedirectHandler);
                    ctx.pipeline().addAfter("login", "heartBeat", new HeartBeatServerHandler());

                    ctx.pipeline().remove("login");
                    log.info("登录成功:" + session.getUser());

                } else {
                    ServerSession.closeSession(ctx);
                    log.info("登录失败:" + session.getUser());

                }

            }
            //异步任务异常
            @Override
            public void onException(Throwable t) {
                ServerSession.closeSession(ctx);
                log.info("登录失败:" + session.getUser());
            }
        });

    }


}