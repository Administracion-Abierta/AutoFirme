package org.java_websocket.framing;

import org.java_websocket.enums.Opcode;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/framing/PongFrame.class */
public class PongFrame extends ControlFrame {
    public PongFrame() {
        super(Opcode.PONG);
    }

    public PongFrame(PingFrame pingFrame) {
        super(Opcode.PONG);
        setPayload(pingFrame.getPayloadData());
    }
}
