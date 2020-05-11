package com.imitee.bleadv;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.HashMap;
import java.util.Map;

/**
 * @author: luo
 * @create: 2020-05-10 13:10
 **/
public class JsonUtils {
    public static String mapToJson(Map<?, ?> raw) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, Object> map = convertMap(raw);

        return gson.toJson(map);
    }

    private static Map<String, Object> convertMap(Map<?, ?> raw) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<?, ?> entry : raw.entrySet()) {

            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value instanceof Map<?, ?>) {
                map.put(key, convertMap((Map<?, ?>) value));
            } else {
                map.put(key, value.toString());
            }


        }
        return map;
    }


}
