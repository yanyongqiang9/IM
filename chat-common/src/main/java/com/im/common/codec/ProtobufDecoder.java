package com.im.common.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import com.im.common.InvalidFrameException;
import com.im.common.ProtoInstant;
import com.im.common.bean.ProtoMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ProtobufDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object outMsg = decode0(ctx, in);
        if (outMsg != null) {
            // 获取业务消息
            out.add(outMsg);
        }
    }

    private Object decode0(ChannelHandlerContext ctx, ByteBuf in) throws InvalidProtocolBufferException, InvalidFrameException {
        // 标记一下当前的readIndex的位置
        in.markReaderIndex();
        // 判断包头长度
        if (in.readableBytes() < 8) {// 不够包头
            return null;
        }
        //读取魔数
        short magic = in.readShort();
        if (magic != ProtoInstant.MAGIC_CODE) {
            String error = "客户端口令不对:" + ctx.channel().remoteAddress();
            //异常连接，直接报错，关闭连接
            throw new InvalidFrameException(error);
        }
        //读取版本
        short version = in.readShort();
        if (version != ProtoInstant.VERSION_CODE) {
            String error = "协议的版本不对:" + ctx.channel().remoteAddress();
            //异常连接，直接报错，关闭连接
            throw new InvalidFrameException(error);
        }
        // 读取传送过来的消息的长度。
        int length = in.readInt();

        // 长度如果小于0
        if (length < 0) {
            // 非法数据，关闭连接
            ctx.close();
        }
        if (length > in.readableBytes()) {// 读到的消息体长度如果小于传送过来的消息长度
            // 重置读取位置
            in.resetReaderIndex();
            return null;
        }
        byte[] array;
        if (in.hasArray()) {
            //堆缓冲
//            ByteBuf slice = in.slice();
            //小伙伴 calvin 发现的bug，这里指正读取  length 长度
//            ByteBuf slice = in.slice(in.readerIndex(), length);
//            Logger.cfo("slice length=" + slice.readableBytes());
//            array = slice.array();
            array = in.array();
        } else {
            //直接缓冲
            array = new byte[length];
            in.readBytes(array, 0, length);
        }

        // 字节转成对象
        return ProtoMsg.Message.parseFrom(array);
    }
}
