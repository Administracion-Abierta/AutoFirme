package org.java_websocket.framing;

import java.nio.ByteBuffer;
import org.java_websocket.enums.Opcode;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidFrameException;
import org.java_websocket.util.ByteBufferUtils;
import org.java_websocket.util.Charsetfunctions;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/framing/CloseFrame.class */
public class CloseFrame extends ControlFrame {
    public static final int NORMAL = 1000;
    public static final int GOING_AWAY = 1001;
    public static final int PROTOCOL_ERROR = 1002;
    public static final int REFUSE = 1003;
    public static final int NOCODE = 1005;
    public static final int ABNORMAL_CLOSE = 1006;
    public static final int NO_UTF8 = 1007;
    public static final int POLICY_VALIDATION = 1008;
    public static final int TOOBIG = 1009;
    public static final int EXTENSION = 1010;
    public static final int UNEXPECTED_CONDITION = 1011;
    public static final int SERVICE_RESTART = 1012;
    public static final int TRY_AGAIN_LATER = 1013;
    public static final int BAD_GATEWAY = 1014;
    public static final int TLS_ERROR = 1015;
    public static final int NEVER_CONNECTED = -1;
    public static final int BUGGYCLOSE = -2;
    public static final int FLASHPOLICY = -3;
    private int code;
    private String reason;

    public CloseFrame() {
        super(Opcode.CLOSING);
        setReason("");
        setCode(1000);
    }

    public void setCode(int code) {
        this.code = code;
        if (code == 1015) {
            this.code = 1005;
            this.reason = "";
        }
        updatePayload();
    }

    public void setReason(String reason) {
        if (reason == null) {
            reason = "";
        }
        this.reason = reason;
        updatePayload();
    }

    public int getCloseCode() {
        return this.code;
    }

    public String getMessage() {
        return this.reason;
    }

    @Override // org.java_websocket.framing.FramedataImpl1
    public String toString() {
        return super.toString() + "code: " + this.code;
    }

    @Override // org.java_websocket.framing.ControlFrame, org.java_websocket.framing.FramedataImpl1
    public void isValid() throws InvalidDataException {
        super.isValid();
        if (this.code == 1007 && this.reason.isEmpty()) {
            throw new InvalidDataException(1007, "Received text is no valid utf8 string!");
        }
        if (this.code == 1005 && 0 < this.reason.length()) {
            throw new InvalidDataException(1002, "A close frame must have a closecode if it has a reason");
        }
        if (this.code > 1015 && this.code < 3000) {
            throw new InvalidDataException(1002, "Trying to send an illegal close code!");
        }
        if (this.code == 1006 || this.code == 1015 || this.code == 1005 || this.code > 4999 || this.code < 1000 || this.code == 1004) {
            throw new InvalidFrameException("closecode must not be sent over the wire: " + this.code);
        }
    }

    @Override // org.java_websocket.framing.FramedataImpl1
    public void setPayload(ByteBuffer payload) {
        this.code = 1005;
        this.reason = "";
        payload.mark();
        if (payload.remaining() == 0) {
            this.code = 1000;
            return;
        }
        if (payload.remaining() == 1) {
            this.code = 1002;
            return;
        }
        if (payload.remaining() >= 2) {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.position(2);
            bb.putShort(payload.getShort());
            bb.position(0);
            this.code = bb.getInt();
        }
        payload.reset();
        try {
            int mark = payload.position();
            validateUtf8(payload, mark);
        } catch (InvalidDataException e) {
            this.code = 1007;
            this.reason = null;
        }
    }

    private void validateUtf8(ByteBuffer payload, int mark) throws InvalidDataException {
        try {
            try {
                payload.position(payload.position() + 2);
                this.reason = Charsetfunctions.stringUtf8(payload);
                payload.position(mark);
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException(1007);
            }
        } catch (Throwable th) {
            payload.position(mark);
            throw th;
        }
    }

    private void updatePayload() {
        byte[] by = Charsetfunctions.utf8Bytes(this.reason);
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(this.code);
        buf.position(2);
        ByteBuffer pay = ByteBuffer.allocate(2 + by.length);
        pay.put(buf);
        pay.put(by);
        pay.rewind();
        super.setPayload(pay);
    }

    @Override // org.java_websocket.framing.FramedataImpl1, org.java_websocket.framing.Framedata
    public ByteBuffer getPayloadData() {
        if (this.code == 1005) {
            return ByteBufferUtils.getEmptyByteBuffer();
        }
        return super.getPayloadData();
    }

    @Override // org.java_websocket.framing.FramedataImpl1
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        CloseFrame that = (CloseFrame) o;
        if (this.code != that.code) {
            return false;
        }
        if (this.reason != null) {
            return this.reason.equals(that.reason);
        }
        return that.reason == null;
    }

    @Override // org.java_websocket.framing.FramedataImpl1
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * result) + this.code)) + (this.reason != null ? this.reason.hashCode() : 0);
    }
}
