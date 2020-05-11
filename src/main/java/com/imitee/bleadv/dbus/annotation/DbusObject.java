package com.imitee.bleadv.dbus.annotation;

import org.freedesktop.dbus.interfaces.DBusInterface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: luo
 * @create: 2020-05-10 15:27
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbusObject {
    Class<? extends DBusInterface> value();

}
