package com.imitee.bleadv.dbus.core;

import com.imitee.bleadv.dbus.adapters.ConvertAdapter;
import com.imitee.bleadv.dbus.adapters.DefaultConvertAdapter;
import com.imitee.bleadv.dbus.annotation.DbusObject;
import com.imitee.bleadv.dbus.annotation.ObjectOperation;
import com.imitee.bleadv.dbus.annotation.OperationType;
import com.imitee.bleadv.dbus.annotation.PropertyOperation;
import com.imitee.bleadv.lib.base.BusConnector;

import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.interfaces.Properties;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author: luo
 * @create: 2020-05-10 15:39
 **/
public class ObjectHandler<T> implements InvocationHandler {

    private final Class<T> rClass;
    private final String objectPath;
    private final Properties properties;
    private final String interfaceName;
    private final DBusInterface busObject;

    private ConvertAdapter convertAdapter = new DefaultConvertAdapter();

    public ObjectHandler(Class<T> rClass, String objectPath) throws DBusException {
        this(rClass, objectPath, BusConnector.getInstance().requireObject(objectPath, Properties.class));
    }

    public ObjectHandler(Class<T> rClass, String objectPath, Properties properties) throws DBusException {
        this.rClass = rClass;
        this.objectPath = objectPath;
        this.properties = properties;
        Class<? extends DBusInterface> classOfT = getDbusClass();
        this.interfaceName = classOfT.getName();
        this.busObject = BusConnector.getInstance().requireObject(objectPath, classOfT);
    }


    public ObjectHandler(Class<T> rClass, DBusInterface busObject) throws DBusException {
        this.rClass = rClass;
        this.objectPath = busObject.getObjectPath();
        this.properties = BusConnector.getInstance().requireObject(objectPath, Properties.class);
        Class<? extends DBusInterface> classOfT = getDbusClass();
        this.interfaceName = classOfT.getName();
        this.busObject = busObject;
    }

    public ObjectHandler(Class<T> rClass, DBusInterface busObject, Properties properties) {
        this.rClass = rClass;
        this.objectPath = busObject.getObjectPath();
        this.properties = properties;
        Class<? extends DBusInterface> classOfT = getDbusClass();
        this.interfaceName = classOfT.getName();
        this.busObject = busObject;
    }

    protected Class<? extends DBusInterface> getDbusClass() {
        return rClass.getAnnotation(DbusObject.class).value();
    }

    public void setConvertAdapter(ConvertAdapter convertAdapter) {
        this.convertAdapter = convertAdapter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        PropertyOperation propertyOperation = method.getAnnotation(PropertyOperation.class);
        if (propertyOperation != null) {
            return handlePropertyOperation(method, args, propertyOperation);
        }

        ObjectOperation objectOperation = method.getAnnotation(ObjectOperation.class);
        if (objectOperation != null) {
            return handleObjectOperation(proxy, method, args, objectOperation);
        }

//        System.out.println("Warning:skipping method:"+method);
        return null;


    }

    private Object handleObjectOperation(Object proxy, Method method, Object[] args, ObjectOperation objectOperation) throws Throwable {
        String name = objectOperation.name();

        name = parseObjectMethodName(method, name);


        Class[] classes = method.getParameterTypes();


        Class<? extends DBusInterface> objectClass = busObject.getClass();
        Method targetMethod=null;
        try {
            targetMethod = objectClass.getMethod(name, classes);
            targetMethod.setAccessible(true);
            return targetMethod.invoke(busObject, args);
        } catch (IllegalAccessException e) {
            throw new HandleInvocationException(String.format("Invoke method fail ( %s->%s )", name, targetMethod), e);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } catch (NoSuchMethodException e) {
            throw new HandleInvocationException("Can not find target method for " + name, e);
        }

    }

    private Object handlePropertyOperation(Method method, Object[] args, PropertyOperation propertyOperation) {
        OperationType operationType = propertyOperation.type();
        String field = propertyOperation.name();

        operationType = parseOperationType(method, operationType);
        field = parsePropertyName(method, field);


        if (operationType == OperationType.GET) {
            Object get = properties.Get(interfaceName, field);
            Class<?> returnType = method.getReturnType();

            Object ret = convertAdapter.adaptReturn(returnType, get);


            if (returnType.isPrimitive() && ret == null) {
                String message = String.format("Return type is [%s] ,but return null,you may change to non primitive type?", returnType.getSimpleName());
                throw new HandleInvocationException(message);
            }

            return ret;
        }
        if (args == null) {
            throw new HandleInvocationException("Setter args can not be null,require :" + method.toString());
        }

        if (args.length != 1) {
            throw new HandleInvocationException("Setter can only have one parameter,current:" + args);
        }

        Object arg = convertAdapter.adaptArg(method.getParameterTypes()[0], args[0]);
        properties.Set(interfaceName, field, arg);
        return null;
    }

    private String parsePropertyName(Method method, String name) {
        if (name != null && name.length() > 0) {
            return name;
        }
        String methodName = method.getName();

        int length = methodName.length();

        if (methodName.startsWith("set") && length > 3) {
            return methodName.substring(3);
        }
        if (methodName.startsWith("get") && length > 3) {
            return methodName.substring(3);
        }
        if (methodName.startsWith("is") && length > 2) {
            return methodName.substring(2);
        }

        throw new HandleInvocationException("can not parse property name of method:" + method.getName());

    }

    private String parseObjectMethodName(Method method, String name) {
        if (name != null && name.length() > 0) {
            return name;
        }

        return method.getName();
    }

    private OperationType parseOperationType(Method method, OperationType operationType) {
        if (operationType != OperationType.AUTO) {
            return operationType;
        }

        String methodName = method.getName();
        if (methodName.startsWith("set")) {
            return OperationType.SET;
        }
        if (methodName.startsWith("get") || methodName.startsWith("is")) {
            return OperationType.GET;
        }
        throw new HandleInvocationException("can not parse operation type of method:" + method.getName());
    }

}
