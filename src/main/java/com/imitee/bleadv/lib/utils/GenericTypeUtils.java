package com.imitee.bleadv.lib.utils;

import java.lang.reflect.ParameterizedType;

@SuppressWarnings("unchecked")
public class GenericTypeUtils {
    public static <T> Class<T> getClassOf(Object object) {
        ParameterizedType genericSuperclass = (ParameterizedType) object.getClass().getGenericSuperclass();
        return (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    public static <T> Class<T> getClassOf(Object object, int index) {
        return (Class<T>) ((ParameterizedType) object.getClass().getGenericSuperclass()).getActualTypeArguments()[index];
    }

    public static <T> Class<T> getInterfaceClassOf(Object object, int interfaceIndex, int paramIndex) {
        return (Class<T>) ((ParameterizedType) object.getClass().getGenericInterfaces()[interfaceIndex]).getActualTypeArguments()[paramIndex];
    }


    public static <T> boolean isInstance(Object object,Class<T> tClass) {
        try {
            return tClass.isInstance(object);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}