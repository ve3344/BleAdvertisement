package com.imitee.bleadv.dbus.adapters;

import com.imitee.bleadv.lib.utils.GenericTypeUtils;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.types.UInt16;
import org.freedesktop.dbus.types.UInt32;

import java.util.function.Function;

/**
 * @author: luo
 * @create: 2020-05-10 19:55
 **/
public class DbusConvertAdapter implements ConvertAdapter {


    @Override
    public Object adaptArg(Class<?> paramType, Object arg) {
        //int -> Uint32
        arg = forceAdaptType(paramType, arg, Integer.class, UInt32.class, UInt32::new);
        arg = tryAdaptType(paramType, arg, String.class, DBusPath.class, DBusPath::new);

        return arg;
    }


    @Override
    public Object adaptReturn(Class<?> returnType, Object ret) {

        //UInt32->int
        //目标是Inter,实际是Number，则将Number转为Integer
        ret = tryAdaptType( returnType, ret, UInt32.class, Integer.class, UInt32::intValue);
        ret = tryAdaptType( returnType, ret, UInt32.class, Integer.TYPE, UInt32::intValue);

        ret = tryAdaptType(returnType, ret, DBusPath.class, String.class, DBusPath::getPath);
        return ret;
    }

    private static <I, O> Object tryAdaptType(Class<?> methodTargetType, Object actual, Class<I> from, Class<O> to, Function<I, O> function) {
        if (methodTargetType.isAssignableFrom(to) && from.isInstance(actual)) {
            return function.apply((I) actual);
        }
        return actual;
    }

    private static <I, O> Object forceAdaptType( Class<?> methodTargetType,Object actual, Class<I> from, Class<O> to, Function<I, O> function) {
        if (from.isInstance(actual)) {
            return function.apply((I) actual);
        }
        return actual;
    }



    public static void main(String[] args) {
        DBusPath dBusPath = new DBusPath("sa");
        System.out.println(GenericTypeUtils.isInstance(dBusPath, DBusPath.class));
    }

}
