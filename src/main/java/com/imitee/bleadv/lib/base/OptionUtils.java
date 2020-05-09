package com.imitee.bleadv.lib.base;

import org.freedesktop.dbus.types.UInt16;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;

/**
 * @author: luo
 * @create: 2020-05-09 11:04
 **/
public class OptionUtils {
    public static int getUnit16(Map<String, Variant<?>> options, String name, int defaultValue) {
        int value = defaultValue;
        Integer object = getObject(options, name,value );
        return object;
    }

    public static String getString(Map<String, Variant<?>> options, String name, String defaultValue) {
        return getObject(options, name, defaultValue);
    }

    public static<T> T getObject(Map<String, Variant<?>> options, String name, T defaultValue) {
        T value = defaultValue;
        try {
            Variant offsetVariant = options.get(name);
            if (offsetVariant != null) {
                T offsetVariantValue = (T) offsetVariant.getValue();
                if (offsetVariantValue != null) {
                    value = offsetVariantValue;
                }
            }
        }catch (ClassCastException ce){
            throw ce;
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }

        return value;
    }

    public static void print(Map<String, Variant<?>> options) {
        for (Map.Entry<String, Variant<?>> entry : options.entrySet()) {
            Variant<?> value = entry.getValue();
            System.out.printf("[%20s -> %20s][%s,%s] %n", entry.getKey(), value.getValue(),value.getType(),value.getSig());
        }
    }
}
