package com.im.server.processor;

import com.im.common.bean.ProtoMsg;
import com.im.server.session.ServerSession;
import com.im.server.session.SessionMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class ChatRedirectProcessor implements ServerProcessor {
    @Override
    public ProtoMsg.HeadType type() {
        return ProtoMsg.HeadType.MESSAGE_REQUEST;
    }

    @Override
    public boolean action(ServerSession fromSession, ProtoMsg.Message proto) {
        // 聊天处理
        ProtoMsg.MessageRequest msg = proto.getMessageRequest();
//        Logger.tcfo("chatMsg | from="
//                + msg.getFrom()
//                + " , to=" + msg.getTo()
//                + " , content=" + msg.getContent());
        //TODO Logger.tcfo
        log.info("chatMsg | from="
                + msg.getFrom()
                + " , to=" + msg.getTo()
                + " , content=" + msg.getContent());
        // 获取接收方的chatID
        String to = msg.getTo();
        // int platform = msg.getPlatform();
        List<ServerSession> toSessions = SessionMap.inst().getSessionsBy(to);
        if (toSessions == null) {
            //接收方离线
            //TODO Logger.tcfo
            log.info("[" + to + "] 不在线，发送失败!");
        } else {
            toSessions.forEach((session) -> {
                // 将IM消息发送到接收方
                session.writeAndFlush(proto);
            });
        }
        return true;
    }

}