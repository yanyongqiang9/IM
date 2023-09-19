package com.im.client.command;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class LogoutConsoleCommand implements BaseCommand {
    public static final String KEY = "10";

    @Override
    public void exec(Scanner scanner) {
        System.out.println("退出命令执行成功");
    }


    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getTip() {
        return "退出";
    }

}