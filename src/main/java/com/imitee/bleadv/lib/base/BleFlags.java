package com.imitee.bleadv.lib.base;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: luo
 * @create: 2020-05-08 20:49
 **/
public class BleFlags {
    public static final String[] FLAGS_STRING = {
            "read",
            "write",
            "notify",
            "write-without-response",
            "indicate",
    };
    public static final int NONE = 0;

    public static final int READ = 1<<0;
    public static final int WRITE = 1<<1;
    public static final int NOTIFY = 1<<2;
    public static final int WRITE_WITHOUT_RESPONSE = 1<<3;
    public static final int INDICATE = 1<<4;



    public static final int READ_WRITE_NOTIFY = READ | WRITE | NOTIFY;


    public static String[] makeArray(int flag) {
        List<String> flagsList = new ArrayList<>();
        for (int i = 0; i < FLAGS_STRING.length; i++) {
            if ((flag & (1 << i)) != 0) {
                flagsList.add(FLAGS_STRING[i]);
            }
        }
        return flagsList.toArray(new String[0]);
    }


}
