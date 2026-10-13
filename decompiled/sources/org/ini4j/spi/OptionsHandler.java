package org.ini4j.spi;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/OptionsHandler.class */
public interface OptionsHandler extends HandlerBase {
    void endOptions();

    @Override // org.ini4j.spi.HandlerBase
    void handleComment(String str);

    @Override // org.ini4j.spi.HandlerBase
    void handleOption(String str, String str2);

    void startOptions();
}
