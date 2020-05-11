package com.imitee.bleadv.dbus.test;

/**
 * @author: luo
 * @create: 2020-05-11 13:07
 **/
public class LinuxColorUtils {
    public static final int BLACK=0;
    public static final int RED=1;
    public static final int GREEN=2;
    public static final int YELLOW=3;
    public static final int BLUE=4;
    public static final int FUCHSIA=0;
    public static final int CYAN=0;
    public static final int WHITE=7;

    public static void start(int fg,int bg) {
        System.out.printf("\033[%d;%dm", bg+40,fg+30);
    }

    public static void fg(int fg) {
        System.out.printf("\033[%dm", fg+30);
    }

    public static void bg(int bg) {
        System.out.printf("\033[%dm", bg+40);
    }

    public static void clear() {
        System.out.print("\033[0m");
    }
}
