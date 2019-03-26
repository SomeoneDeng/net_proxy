package me.dqn;

import me.dqn.context.ClientManager;

/**
 * @author dqn
 * created at 2019/3/12 1:48
 */
public class ClientApp {
    public static void main(String[] args) {
        try {
            ClientManager.getINSTANCE().start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
