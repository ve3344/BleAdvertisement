package com.imitee.bleadv.dbus.core;

/**
 * @author: luo
 * @create: 2020-05-11 12:59
 **/
public class HandleInvocationException extends RuntimeException{
    public HandleInvocationException() {
    }

    public HandleInvocationException(String message) {
        super(message);
    }

    public HandleInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandleInvocationException(Throwable cause) {
        super(cause);
    }
}
