package com.qull.springarch.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 17:13
 */
public class MessageTracker {
    private static final List<String> MESSAGE = new ArrayList<>();

    public static void addMsg(String message) {
        MESSAGE.add(message);
    }

    public static void clear() {
        MESSAGE.clear();
    }

    public static String getMsg(int index) {
        return MESSAGE.get(index);
    }

    public static List<String> getMsgs() {
        return MESSAGE;
    }
}
