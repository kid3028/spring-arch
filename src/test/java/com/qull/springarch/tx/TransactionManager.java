package com.qull.springarch.tx;

import com.qull.springarch.util.MessageTracker;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 15:33
 */
public class TransactionManager {
    public void start() {
        System.out.println("start tx...");
        MessageTracker.addMsg("start tx...");
    }

    public void commit() {
        System.out.println("commit tx...");
        MessageTracker.addMsg("commit tx...");
    }

    public void rollback() {
        System.out.println("rollback tx...");
        MessageTracker.addMsg("rollback tx...");
    }
}
