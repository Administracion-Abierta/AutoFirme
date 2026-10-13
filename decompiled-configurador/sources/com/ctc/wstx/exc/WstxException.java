package com.ctc.wstx.exc;

import com.ctc.wstx.util.StringUtil;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/exc/WstxException.class */
public class WstxException extends XMLStreamException {
    protected final String mMsg;

    public WstxException(String msg) {
        super(msg);
        this.mMsg = msg;
    }

    public WstxException(Throwable th) {
        super(th.getMessage(), th);
        this.mMsg = th.getMessage();
    }

    public WstxException(String msg, Location loc) {
        super(msg, loc);
        this.mMsg = msg;
    }

    public WstxException(String msg, Location loc, Throwable th) {
        super(msg, loc, th);
        this.mMsg = msg;
    }

    public String getMessage() {
        String locMsg = getLocationDesc();
        if (locMsg == null) {
            return super.getMessage();
        }
        StringBuilder sb = new StringBuilder(this.mMsg.length() + locMsg.length() + 20);
        sb.append(this.mMsg);
        StringUtil.appendLF(sb);
        sb.append(" at ");
        sb.append(locMsg);
        return sb.toString();
    }

    public String toString() {
        return getClass().getName() + ": " + getMessage();
    }

    protected String getLocationDesc() {
        Location loc = getLocation();
        if (loc == null) {
            return null;
        }
        return loc.toString();
    }
}
