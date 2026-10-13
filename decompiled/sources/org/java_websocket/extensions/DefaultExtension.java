package org.java_websocket.extensions;

import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidFrameException;
import org.java_websocket.framing.Framedata;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/extensions/DefaultExtension.class */
public class DefaultExtension implements IExtension {
    @Override // org.java_websocket.extensions.IExtension
    public void decodeFrame(Framedata inputFrame) throws InvalidDataException {
    }

    @Override // org.java_websocket.extensions.IExtension
    public void encodeFrame(Framedata inputFrame) {
    }

    @Override // org.java_websocket.extensions.IExtension
    public boolean acceptProvidedExtensionAsServer(String inputExtension) {
        return true;
    }

    @Override // org.java_websocket.extensions.IExtension
    public boolean acceptProvidedExtensionAsClient(String inputExtension) {
        return true;
    }

    @Override // org.java_websocket.extensions.IExtension
    public void isFrameValid(Framedata inputFrame) throws InvalidDataException {
        if (inputFrame.isRSV1() || inputFrame.isRSV2() || inputFrame.isRSV3()) {
            throw new InvalidFrameException("bad rsv RSV1: " + inputFrame.isRSV1() + " RSV2: " + inputFrame.isRSV2() + " RSV3: " + inputFrame.isRSV3());
        }
    }

    @Override // org.java_websocket.extensions.IExtension
    public String getProvidedExtensionAsClient() {
        return "";
    }

    @Override // org.java_websocket.extensions.IExtension
    public String getProvidedExtensionAsServer() {
        return "";
    }

    @Override // org.java_websocket.extensions.IExtension
    public IExtension copyInstance() {
        return new DefaultExtension();
    }

    @Override // org.java_websocket.extensions.IExtension
    public void reset() {
    }

    @Override // org.java_websocket.extensions.IExtension
    public String toString() {
        return getClass().getSimpleName();
    }

    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean equals(Object o) {
        return this == o || (o != null && getClass() == o.getClass());
    }
}
