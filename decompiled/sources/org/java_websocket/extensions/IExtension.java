package org.java_websocket.extensions;

import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/extensions/IExtension.class */
public interface IExtension {
    void decodeFrame(Framedata framedata) throws InvalidDataException;

    void encodeFrame(Framedata framedata);

    boolean acceptProvidedExtensionAsServer(String str);

    boolean acceptProvidedExtensionAsClient(String str);

    void isFrameValid(Framedata framedata) throws InvalidDataException;

    String getProvidedExtensionAsClient();

    String getProvidedExtensionAsServer();

    IExtension copyInstance();

    void reset();

    String toString();
}
