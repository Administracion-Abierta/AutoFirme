package org.codehaus.stax2.validation;

import javax.xml.stream.Location;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/validation/XMLValidationProblem.class */
public class XMLValidationProblem {
    public static final int SEVERITY_WARNING = 1;
    public static final int SEVERITY_ERROR = 2;
    public static final int SEVERITY_FATAL = 3;
    protected Location mLocation;
    protected final String mMessage;
    protected final int mSeverity;
    protected String mType;
    protected XMLValidator mReporter;

    public XMLValidationProblem(Location loc, String msg) {
        this(loc, msg, 2);
    }

    public XMLValidationProblem(Location loc, String msg, int severity) {
        this(loc, msg, severity, null);
    }

    public XMLValidationProblem(Location loc, String msg, int severity, String type) {
        this.mLocation = loc;
        this.mMessage = msg;
        this.mSeverity = severity;
        this.mType = type;
    }

    public XMLValidationException toException() {
        return XMLValidationException.createException(this);
    }

    public void setType(String t) {
        this.mType = t;
    }

    public void setLocation(Location l) {
        this.mLocation = l;
    }

    public void setReporter(XMLValidator v) {
        this.mReporter = v;
    }

    public Location getLocation() {
        return this.mLocation;
    }

    public String getMessage() {
        return this.mMessage;
    }

    public int getSeverity() {
        return this.mSeverity;
    }

    public String getType() {
        return this.mType;
    }

    public XMLValidator getReporter() {
        return this.mReporter;
    }
}
