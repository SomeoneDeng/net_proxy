package me.dqn;

import me.dqn.server.Server;

/**
 * @author dqn
 * created at 2019/3/12 1:36
 */
public class ServerApp {
    public static void main(String[] args) throws InterruptedException {
        Server.instance().start();
    }
}
