package com.imitee.bleadv.dbus.core;

import com.imitee.bleadv.dbus.adapters.ConvertAdapter;
import com.imitee.bleadv.dbus.adapters.DefaultConvertAdapter;

import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.interfaces.Properties;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author: luo
 * @create: 2020-05-10 14:51
 **/
public class ModelMaker {
    private ConvertAdapter convertAdapter = new DefaultConvertAdapter();

    public ModelMaker setConvertAdapter(ConvertAdapter convertAdapter) {
        this.convertAdapter = convertAdapter;
        return this;
    }


    @SuppressWarnings("unchecked")
    public <R> R makeObject(Class<R> rClass, DBusInterface dBusInterface) throws DBusException {
        ObjectHandler<R> invocationHandler = new ObjectHandler<>(rClass, dBusInterface);
        invocationHandler.setConvertAdapter(convertAdapter);
        return (R) Proxy.newProxyInstance(rClass.getClassLoader(), new Class<?>[]{rClass}, invocationHandler);
    }

    @SuppressWarnings("unchecked")
    public <R> R makeObject(Class<R> rClass, DBusInterface dBusInterface, Properties properties) {
        ObjectHandler<R> invocationHandler = new ObjectHandler<>(rClass, dBusInterface, properties);
        invocationHandler.setConvertAdapter(convertAdapter);
        return (R) Proxy.newProxyInstance(rClass.getClassLoader(), new Class<?>[]{rClass}, invocationHandler);
    }


}
