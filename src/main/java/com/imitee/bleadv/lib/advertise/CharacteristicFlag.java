package com.imitee.bleadv.lib.advertise;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: luo
 * @create: 2020-05-08 20:49
 **/
public class CharacteristicFlag {
    public static final String[] FLAGS_STRING = {"read", "write", "notify"};
    public static final int NONE = 0x00;
    public static final int READ = 0x01;
    public static final int WRITE = 0x02;
    public static final int NOTIFY = 0x04;
    public static final int ALL = READ | WRITE | NOTIFY;


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
