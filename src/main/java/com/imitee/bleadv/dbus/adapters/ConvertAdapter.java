package com.imitee.bleadv.dbus.adapters;

/**
 * @author: luo
 * @create: 2020-05-10 19:55
 **/
public interface ConvertAdapter {
    Object adaptArg(Class<?> paramType, Object arg);

    Object adaptReturn(Class<?> returnType, Object ret);
}
