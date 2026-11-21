package com.javatv.exception;

/**
 * TV kapalıyken işlem yapılmaya çalışıldığında fırlatılır.
 */
public class TvPoweredOffException extends TvException {
    public TvPoweredOffException() {
        super("İşlem gerçekleştirilemedi: Televizyon kapalı.");
    }

    public TvPoweredOffException(String action) {
        super("'" + action + "' işlemi gerçekleştirilemedi: Televizyon kapalı. Lütfen önce TV'yi açınız.");
    }
}
