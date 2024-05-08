package org.minitestlang.listener.minitestlang;

public class ListenerException extends RuntimeException {
    public ListenerException(String message) {
        super(message);
    }

    public ListenerException(String message, Throwable cause) {
        super(message, cause);
    }
}
