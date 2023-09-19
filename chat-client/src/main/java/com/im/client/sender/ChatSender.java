package com.im.client.sender;

import com.im.client.protoConverter.ChatMsgConverter;
import com.im.common.bean.ChatMsg;
import com.im.common.bean.ProtoMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatSender extends BaseSender {

    public void sendChatMsg(String touid, String content) {
        log.info("发送消息 startConnectServer");
        ChatMsg chatMsg = new ChatMsg(getUser());
        chatMsg.setContent(content);
        chatMsg.setMsgType(ChatMsg.MSGTYPE.TEXT);
        chatMsg.setTo(touid);
        chatMsg.setMsgId(System.currentTimeMillis());
        ProtoMsg.Message message = ChatMsgConverter.build(chatMsg, getUser(), getSession());
        super.sendMsg(message);
    }

    @Override
    protected void sendSucced(ProtoMsg.Message message) {
        log.info("发送成功:" + message.getMessageRequest().getContent());
    }


    @Override
    protected void sendfailed(ProtoMsg.Message message) {
        log.info("发送失败:" + message.getMessageRequest().getContent());
    }
}