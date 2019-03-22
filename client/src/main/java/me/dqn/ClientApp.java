package me.dqn;

import me.dqn.client.Client;

/**
 * @author dqn
 * created at 2019/3/12 1:48
 */
public class ClientApp {
    public static void main(String[] args) throws InterruptedException {
        Client.getInstance().startRegister();
    }
}
