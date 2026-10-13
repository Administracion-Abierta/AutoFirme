package org.ini4j.spi;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/IniHandler.class */
public interface IniHandler extends HandlerBase {
    void endIni();

    void endSection();

    @Override // org.ini4j.spi.HandlerBase
    void handleComment(String str);

    @Override // org.ini4j.spi.HandlerBase
    void handleOption(String str, String str2);

    void startIni();

    void startSection(String str);
}
