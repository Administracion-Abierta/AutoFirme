package org.slf4j.spi;

import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/slf4j/spi/MDCAdapter.class */
public interface MDCAdapter {
    void put(String str, String str2);

    String get(String str);

    void remove(String str);

    void clear();

    Map<String, String> getCopyOfContextMap();

    void setContextMap(Map<String, String> map);
}
