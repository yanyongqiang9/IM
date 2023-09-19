package com.im.client.protoConverter;

import com.im.client.session.ClientSession;
import com.im.common.bean.ProtoMsg;
import com.im.common.bean.User;

public class HeartBeatMsgConverter extends BaseConverter {
    private final User user;

    public HeartBeatMsgConverter(User user, ClientSession session) {
        super(ProtoMsg.HeadType.HEART_BEAT, session);
        this.user = user;
    }

    public ProtoMsg.Message build() {

        ProtoMsg.Message.Builder outerBuilder = getOuterBuilder(-1);

        ProtoMsg.MessageHeartBeat.Builder inner =
                ProtoMsg.MessageHeartBeat.newBuilder()
                        .setSeq(0)
                        .setJson("{\"from\":\"client\"}")
                        .setUid(user.getUid());
        return outerBuilder.setHeartBeat(inner).build();
    }

}
