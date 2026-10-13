package org.slf4j.spi;

import org.slf4j.IMarkerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/slf4j/spi/MarkerFactoryBinder.class */
public interface MarkerFactoryBinder {
    IMarkerFactory getMarkerFactory();

    String getMarkerFactoryClassStr();
}
