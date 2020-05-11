package com.imitee.bleadv.dbus.adapters;

/**
 * @author: luo
 * @create: 2020-05-10 19:55
 **/
public class DefaultConvertAdapter implements ConvertAdapter{

    @Override
    public Object adaptArg(Class<?> paramType, Object arg) {
        return arg;
    }

    @Override
    public Object adaptReturn(Class<?> returnType, Object ret) {
        return ret;
    }
}
