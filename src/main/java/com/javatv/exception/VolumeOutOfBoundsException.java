package com.javatv.exception;

/**
 * Ses seviyesi belirlenen alt/üst sınırların dışına çıkmaya çalıştığında fırlatılır.
 */
public class VolumeOutOfBoundsException extends TvException {
    public VolumeOutOfBoundsException(String message) {
        super(message);
    }
}
