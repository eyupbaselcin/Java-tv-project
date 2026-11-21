package com.javatv.exception;

/**
 * İstenen kanal numarası veya adı bulunamadığında fırlatılır.
 */
public class ChannelNotFoundException extends TvException {
    public ChannelNotFoundException(int channelNumber) {
        super(channelNumber + " numaralı kanal bulunamadı.");
    }

    public ChannelNotFoundException(String channelName) {
        super("'" + channelName + "' isimli kanal bulunamadı.");
    }
}
