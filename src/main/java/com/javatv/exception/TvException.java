package com.javatv.exception;

/**
 * TV operasyonları için temel istisna sınıfı.
 */
public class TvException extends RuntimeException {
    public TvException(String message) {
        super(message);
    }

    public TvException(String message, Throwable cause) {
        super(message, cause);
    }
}
