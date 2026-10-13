package org.ini4j.spi;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/HandlerBase.class */
interface HandlerBase {
    void handleComment(String str);

    void handleOption(String str, String str2);
}
