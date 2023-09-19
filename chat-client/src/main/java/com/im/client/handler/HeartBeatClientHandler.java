package com.im.client.handler;

import com.im.client.protoConverter.HeartBeatMsgConverter;
import com.im.client.session.ClientSession;
import com.im.common.bean.ProtoMsg;
import com.im.common.bean.User;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
@Service
public class HeartBeatClientHandler extends ChannelHandlerAdapter {
    //心跳的时间间隔，单位为s
    private static final int HEARTBEAT_INTERVAL = 50;

    //在Handler被加入到Pipeline时，开始发送心跳
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientSession session = ClientSession.getSession(ctx);
        User user = session.getUser();
        HeartBeatMsgConverter builder = new HeartBeatMsgConverter(user, session);

        ProtoMsg.Message message = builder.build();
        //发送心跳
        heartBeat(ctx, message);
    }


    //使用定时器，发送心跳报文
    public void heartBeat(ChannelHandlerContext ctx, ProtoMsg.Message heartbeatMsg) {
        ctx.executor().schedule(() -> {

            if (ctx.channel().isActive()) {
                log.info(" 发送 HEART_BEAT  消息 to server");
                ctx.writeAndFlush(heartbeatMsg);

                //递归调用，发送下一次的心跳
                heartBeat(ctx, heartbeatMsg);
            }

        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * 接受到服务器的心跳回写
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //判断消息实例
        if (!(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断类型
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = pkg.getType();
        if (headType.equals(ProtoMsg.HeadType.HEART_BEAT)) {
            log.info(" 收到回写的 HEART_BEAT  消息 from server");
            //TODO 这个地方可以设置另外的一个机关，那就是HeartBeatClientHandler可以继承
            //IdleStateHandler类，使得其在完成心跳处理的同时，还能和服务器的空闲检测处理器一样，
            //在客户端进行空闲检测。这样，客户端也可以对服务器进行假死判定，在服务器端假死的情
            //况下，客户端可以发起重连。客户端的空闲检测的实战就留给大家去自行实验
        } else {
            super.channelRead(ctx, msg);
        }

    }

}