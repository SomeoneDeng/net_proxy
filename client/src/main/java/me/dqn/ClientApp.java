package me.dqn;

import me.dqn.client.ClientContext;

/**
 * @author dqn
 * created at 2019/3/12 1:48
 */
public class ClientApp {
    public static void main(String[] args) {
        try {
            ClientContext.getINSTANCE().start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
