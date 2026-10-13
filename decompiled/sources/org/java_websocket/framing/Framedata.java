package org.java_websocket.framing;

import java.nio.ByteBuffer;
import org.java_websocket.enums.Opcode;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/framing/Framedata.class */
public interface Framedata {
    boolean isFin();

    boolean isRSV1();

    boolean isRSV2();

    boolean isRSV3();

    boolean getTransfereMasked();

    Opcode getOpcode();

    ByteBuffer getPayloadData();

    void append(Framedata framedata);
}
