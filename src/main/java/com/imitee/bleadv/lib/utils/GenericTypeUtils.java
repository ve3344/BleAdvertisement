package com.imitee.bleadv.lib.utils;

import java.lang.reflect.ParameterizedType;

@SuppressWarnings("unchecked")
public class GenericTypeUtils {
    public static <T> Class<T> getClassOf(Object object) {
        return (Class<T>) ((ParameterizedType) object.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public static <T> Class<T> getClassOf() {
        return getClassOf(new Obj<T>());
    }
    private static class Obj<T>{

    }
}