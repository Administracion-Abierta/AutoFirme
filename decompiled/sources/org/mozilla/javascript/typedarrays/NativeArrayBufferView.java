package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/typedarrays/NativeArrayBufferView.class */
public abstract class NativeArrayBufferView extends IdScriptableObject {
    private static final long serialVersionUID = 6884475582973958419L;
    private static Boolean useLittleEndian = null;
    protected final NativeArrayBuffer arrayBuffer;
    protected final int offset;
    protected final int byteLength;
    private static final int Id_buffer = 1;
    private static final int Id_byteOffset = 2;
    private static final int Id_byteLength = 3;
    protected static final int MAX_INSTANCE_ID = 3;

    public NativeArrayBufferView() {
        this.arrayBuffer = new NativeArrayBuffer();
        this.offset = 0;
        this.byteLength = 0;
    }

    protected NativeArrayBufferView(NativeArrayBuffer ab, int offset, int byteLength) {
        this.offset = offset;
        this.byteLength = byteLength;
        this.arrayBuffer = ab;
    }

    public NativeArrayBuffer getBuffer() {
        return this.arrayBuffer;
    }

    public int getByteOffset() {
        return this.offset;
    }

    public int getByteLength() {
        return this.byteLength;
    }

    protected static boolean useLittleEndian() {
        if (useLittleEndian == null) {
            Context ctx = Context.getCurrentContext();
            if (ctx == null) {
                return false;
            }
            useLittleEndian = Boolean.valueOf(ctx.hasFeature(19));
        }
        return useLittleEndian.booleanValue();
    }

    protected static boolean isArg(Object[] args, int i) {
        return args.length > i && !Undefined.instance.equals(args[i]);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int getMaxInstanceId() {
        return 3;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected String getInstanceIdName(int id) {
        switch (id) {
            case 1:
                return "buffer";
            case 2:
                return "byteOffset";
            case 3:
                return "byteLength";
            default:
                return super.getInstanceIdName(id);
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected Object getInstanceIdValue(int id) {
        switch (id) {
            case 1:
                return this.arrayBuffer;
            case 2:
                return ScriptRuntime.wrapInt(this.offset);
            case 3:
                return ScriptRuntime.wrapInt(this.byteLength);
            default:
                return super.getInstanceIdValue(id);
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findInstanceIdInfo(String s) {
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 6) {
            X = "buffer";
            id = 1;
        } else if (s_length == 10) {
            int c = s.charAt(4);
            if (c == 76) {
                X = "byteLength";
                id = 3;
            } else if (c == 79) {
                X = "byteOffset";
                id = 2;
            }
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        if (id == 0) {
            return super.findInstanceIdInfo(s);
        }
        return instanceIdInfo(5, id);
    }
}
