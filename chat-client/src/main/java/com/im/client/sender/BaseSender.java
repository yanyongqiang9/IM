package com.im.client.sender;

import com.im.client.session.ClientSession;
import com.im.common.bean.ProtoMsg;
import com.im.common.bean.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseSender {
    private User user;
    private ClientSession session;

    public boolean isConnected() {
        if (null == session) {
            System.out.println("session is null");
            return false;
        }
        return session.isConnected();
    }

    public void sendMsg(ProtoMsg.Message message) {

        if (null == getSession() || !isConnected()) {
            log.info("连接还没成功");
            return;
        }

        Channel channel = getSession().getChannel();
        ChannelFuture f = channel.writeAndFlush(message);
        f.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future)
                    throws Exception {
                // 回调
                if (future.isSuccess()) {
                    sendSucced(message);
                } else {
                    sendfailed(message);

                }
            }

        });

        /*try {
            f.sync();
        } catch (InterruptedException e) {

            e.printStackTrace();
            sendException(message);
        }*/
    }

    protected void sendSucced(ProtoMsg.Message message) {
        log.info("发送成功");
    }

    protected void sendfailed(ProtoMsg.Message message) {
        log.info("发送失败");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClientSession getSession() {
        return session;
    }

    public void setSession(ClientSession session) {
        this.session = session;
    }
}