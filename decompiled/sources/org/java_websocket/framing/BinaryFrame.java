package org.java_websocket.framing;

import org.java_websocket.enums.Opcode;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/framing/BinaryFrame.class */
public class BinaryFrame extends DataFrame {
    public BinaryFrame() {
        super(Opcode.BINARY);
    }
}
