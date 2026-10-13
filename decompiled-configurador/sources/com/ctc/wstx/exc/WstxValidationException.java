package com.ctc.wstx.exc;

import com.ctc.wstx.util.StringUtil;
import javax.xml.stream.Location;
import org.codehaus.stax2.validation.XMLValidationException;
import org.codehaus.stax2.validation.XMLValidationProblem;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/exc/WstxValidationException.class */
public class WstxValidationException extends XMLValidationException {
    protected WstxValidationException(XMLValidationProblem cause, String msg) {
        super(cause, msg);
    }

    protected WstxValidationException(XMLValidationProblem cause, String msg, Location loc) {
        super(cause, msg, loc);
    }

    public static WstxValidationException create(XMLValidationProblem cause) {
        Location loc = cause.getLocation();
        if (loc == null) {
            return new WstxValidationException(cause, cause.getMessage());
        }
        return new WstxValidationException(cause, cause.getMessage(), loc);
    }

    public String getMessage() {
        String locMsg = getLocationDesc();
        if (locMsg == null) {
            return super.getMessage();
        }
        String msg = getValidationProblem().getMessage();
        StringBuilder sb = new StringBuilder(msg.length() + locMsg.length() + 20);
        sb.append(msg);
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
