package com.im.client.sender;

import com.im.client.protoConverter.LoginMsgConverter;
import com.im.common.bean.ProtoMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginSender extends BaseSender {

    public void sendLoginMsg() {
        if (!isConnected()) {
            log.info("还没有建立连接!");
            return;
        }

        log.info("构造登录消息");
        ProtoMsg.Message message = LoginMsgConverter.build(getUser(), getSession());
        log.info("发送登录消息");
        super.sendMsg(message);
    }
}
