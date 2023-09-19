package com.im.client.protoConverter;

import com.im.client.session.ClientSession;
import com.im.common.bean.ChatMsg;
import com.im.common.bean.ProtoMsg;
import com.im.common.bean.User;

public class ChatMsgConverter extends BaseConverter {


    private ChatMsg chatMsg;
    private User user;


    private ChatMsgConverter(ClientSession session) {
        super(ProtoMsg.HeadType.MESSAGE_REQUEST, session);
    }


    public ProtoMsg.Message build(ChatMsg chatMsg, User user) {

        this.chatMsg = chatMsg;
        this.user = user;

        ProtoMsg.Message.Builder outerBuilder = getOuterBuilder(-1);

        ProtoMsg.MessageRequest.Builder cb = ProtoMsg.MessageRequest.newBuilder();
        //填充字段
        this.chatMsg.fillMsg(cb);

        return outerBuilder.setMessageRequest(cb).build();
    }

    public static ProtoMsg.Message build(ChatMsg chatMsg, User user, ClientSession session) {

        ChatMsgConverter chatMsgConverter = new ChatMsgConverter(session);

        return chatMsgConverter.build(chatMsg, user);

    }


}