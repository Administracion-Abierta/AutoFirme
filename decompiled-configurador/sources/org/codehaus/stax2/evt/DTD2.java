package org.codehaus.stax2.evt;

import javax.xml.stream.events.DTD;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/evt/DTD2.class */
public interface DTD2 extends DTD {
    String getRootName();

    String getSystemId();

    String getPublicId();

    String getInternalSubset();
}
