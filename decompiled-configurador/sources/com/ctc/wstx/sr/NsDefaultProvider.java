package com.ctc.wstx.sr;

import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sr/NsDefaultProvider.class */
public interface NsDefaultProvider {
    boolean mayHaveNsDefaults(String str, String str2);

    void checkNsDefaults(InputElementStack inputElementStack) throws XMLStreamException;
}
