package com.imitee.bleadv.dbus.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: luo
 * @create: 2020-05-10 15:06
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyOperation {
    String name() default "";

    OperationType type() default OperationType.AUTO;
}
