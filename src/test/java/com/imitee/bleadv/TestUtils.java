package com.imitee.bleadv;

import java.util.Map;

/**
 * @author: luo
 * @create: 2020-05-11 17:48
 **/
public class TestUtils {
    public static void printMap(Map<?, ?> map, int level) {
        System.out.println("{");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            for (int i = 0; i < level; i++) {
                System.out.print("\t");
            }
            Object key = entry.getKey();
            System.out.printf("%-30s -> ", key.toString());

            Object value = entry.getValue();
            if (value instanceof Map) {
                printMap((Map) value, level + 1);
            } else {
                System.out.print(value.toString());
            }
            System.out.println();


        }
        System.out.println();
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
        System.out.println("}");
    }
}
