package com.imitee.bleadv.dbus.adapters;

import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.UInt64;

/**
 * @author: luo
 * @create: 2020-05-10 19:55
 **/
public class DbusConvertAdapter implements ConvertAdapter{

    @Override
    public Object adaptArg(Class<?> paramType, Object arg) {
        if (paramType.isAssignableFrom(Integer.TYPE)&&arg instanceof Number){
            return new UInt32(((Number) arg).intValue());
        }
        return arg;
    }

    @Override
    public Object adaptReturn(Class<?> returnType, Object ret) {
        if (returnType.isAssignableFrom(Integer.TYPE)&&ret instanceof Number){
            return ((Number) ret).intValue();
        }
        return ret;
    }
}
