package com.im.client;

import com.im.client.config.SystemConfig;
import com.im.client.handler.ExceptionHandler;
import com.im.client.handler.LoginResponseHandler;
import com.im.client.sender.ChatSender;
import com.im.client.sender.LoginSender;
import com.im.common.bean.User;
import com.im.common.codec.ProtobufDecoder;
import com.im.common.codec.ProtobufEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Data
@Service
public class ChatNettyClient {
    // 服务器ip地址
    @Value("${chat.server.ip}")
    private String host;
    // 服务器端口
    @Value("${chat.server.port}")
    private int port;

    @Resource
    private SystemConfig systemConfig;

    @Resource
    private LoginResponseHandler loginResponseHandler;

    @Resource
    private ExceptionHandler exceptionHandler;


    private Channel channel;
    private ChatSender sender;
    private LoginSender l;

    /**
     * 唯一标记
     */
    private boolean initFlag = true;
    private User user;
    private GenericFutureListener<ChannelFuture> connectedListener;

    private Bootstrap bootstrap;
    private EventLoopGroup g;

    public ChatNettyClient() {
        /*
          客户端的是Bootstrap，服务端的则是 ServerBootstrap。
          都是AbstractBootstrap的子类。
          通过nio方式来接收连接和处理连接
         */
        g = new NioEventLoopGroup(1);
    }

    /**
     * 重连
     */
    public void doConnect() {
        try {
            bootstrap = new Bootstrap();

            bootstrap.group(g);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.remoteAddress(host, port);

            // 设置通道初始化
            bootstrap.handler(
                    new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("decoder", new ProtobufDecoder());
                            ch.pipeline().addLast("encoder", new ProtobufEncoder());
                            ch.pipeline().addLast(loginResponseHandler);
//                            ch.pipeline().addLast(chatMsgHandler);
                            ch.pipeline().addLast(exceptionHandler);
                        }
                    }
            );
            log.info("客户端开始连接 [疯狂创客圈IM]");
            ChannelFuture f = bootstrap.connect();//异步发起连接
            f.addListener(connectedListener);
            // 阻塞
            //f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.info("客户端连接失败!" + e.getMessage());
        }
    }

    public void close() {
        g.shutdownGracefully();
    }


}