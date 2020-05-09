package com.imitee.bleadv.lib.base;

/**
 * @author: luo
 * @create: 2020-05-08 17:03
 **/
public class AdvertiseException extends RuntimeException{
    public AdvertiseException() {
    }

    public AdvertiseException(String message) {
        super(message);
    }

    public AdvertiseException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdvertiseException(Throwable cause) {
        super(cause);
    }
}
