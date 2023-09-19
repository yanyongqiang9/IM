package com.im.server.processor;

import com.im.common.bean.ProtoMsg;
import com.im.server.session.ServerSession;

public interface ServerProcessor {

    ProtoMsg.HeadType type();

    boolean action(ServerSession ch, ProtoMsg.Message proto);

}