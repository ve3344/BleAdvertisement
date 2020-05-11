package com.imitee.bleadv.dbus.test;

import com.imitee.bleadv.dbus.core.HandleInvocationException;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.types.UInt16;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.UInt64;
import org.freedesktop.dbus.types.Variant;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @author: luo
 * @create: 2020-05-10 19:52
 **/
public class Tester {

    private static List<String> ignores = Arrays.asList("RemoveDevice");


    public static void testCallAll(Object object) {
        Class aClass = object.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        System.out.println("----------------------");
        System.out.printf("Testing call %d methods [%s] %n", declaredMethods.length, aClass);

        List<Method> succeeds = new ArrayList<>();
        List<Method> fails = new ArrayList<>();

        int index = 0;
        for (Method declaredMethod : declaredMethods) {
            if (ignores.contains(declaredMethod.getName())) {
                System.out.printf("Ignore[%2d]: [%50s] ", index++, getMethodName(declaredMethod));
                continue;
            }

            System.out.printf("Testing[%2d]: [%-60s] ", index++, getMethodName(declaredMethod));
            declaredMethod.setAccessible(true);
            Object[] args = generateTestArgs(declaredMethod);
            try {
                declaredMethod.invoke(object, args);
                LinuxColorUtils.fg(LinuxColorUtils.GREEN);
                System.out.println(" ---> [ok] ");
                LinuxColorUtils.clear();

                succeeds.add(declaredMethod);
            }catch (HandleInvocationException e){
                StringBuilder sb = new StringBuilder();
                Throwable cause = e;
                while ((cause = cause.getCause()) != null) {
                    sb.append("\n\t\t").append(cause);
                }
                LinuxColorUtils.fg(LinuxColorUtils.RED);
                System.out.println(" ---> [error] " + sb);
                LinuxColorUtils.clear();

                fails.add(declaredMethod);
            } catch (Throwable e) {
                LinuxColorUtils.fg(LinuxColorUtils.GREEN);
                System.out.println(" ---> [ok] but throws :"+e.getCause());
                LinuxColorUtils.clear();

                succeeds.add(declaredMethod);

            }
        }
        System.out.printf("succeed:%d , fail:%d ,total:%d  %n", succeeds.size(), fails.size(), declaredMethods.length);
        System.out.println("----------------------");

    }

    private static String getMethodName(Method method) {
        StringBuilder sb=new StringBuilder();

        sb.append(method.getReturnType().getSimpleName());
        sb.append(" ");
        sb.append(method.getName());
        sb.append("(");
        int index=0;
        for (Class<?> parameterType : method.getParameterTypes()) {
            if (index==0){
                sb.append(parameterType.getSimpleName());
            }else {
                sb.append(",");
                sb.append(parameterType.getSimpleName());
            }
            index++;
        }


        sb.append(")");

/*
        Class<?> declaringClass = declaredMethod.getDeclaringClass();

        String string = declaredMethod.toString();
        string=string.replace(declaringClass.getName()+".","");
        string=string.replace("public final ","");
        int aThrows = string.indexOf("throws");
        if (aThrows>0){

            string=string.substring(0, aThrows);
        }*/
        return sb.toString();
    }

    private static Object[] generateTestArgs(Method declaredMethod) {
        Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
        Object[] objects = new Object[parameterTypes.length];
        Random random = new Random();
        for (int i = 0; i < objects.length; i++) {
            Object value = getRandomValue(parameterTypes[i], random);
            objects[i] = value;
        }

        return objects;
    }

    private static Object getRandomValue(Class<?> parameterType, Random random) {
        if (parameterType.isPrimitive()) {
            if (parameterType.isAssignableFrom(Boolean.TYPE)) {
                return true;
            }
            if (parameterType.isAssignableFrom(Integer.TYPE)) {
                return 22;
            }
            if (parameterType.isAssignableFrom(Long.TYPE)) {
                return 2345;
            }
            if (parameterType.isAssignableFrom(Short.TYPE)) {
                return 3;
            }
        }
        if (parameterType.isAssignableFrom(String.class)) {
            return "hello:" + "random.nextInt()";
        }
        if (parameterType.isAssignableFrom(String[].class)) {
            return new String[]{
                    "1", "2", "3"
            };
        }
        if (parameterType.isAssignableFrom(UInt32.class)) {
            return new UInt32(3);
        }
        if (parameterType.isAssignableFrom(UInt16.class)) {
            return new UInt16(3);
        }
        if (parameterType.isAssignableFrom(UInt64.class)) {
            return new UInt64(3);
        }
        if (parameterType.isAssignableFrom(DBusPath.class)) {
            return new DBusPath("/org/bluez/hci0/dev_54_DA_32_87_0F_54");
        }
        if (parameterType.isAssignableFrom(Map.class)) {
            return new HashMap<String, Variant<?>>();
        }
        return null;
    }

}
