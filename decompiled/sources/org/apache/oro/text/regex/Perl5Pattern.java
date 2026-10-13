package org.apache.oro.text.regex;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/text/regex/Perl5Pattern.class */
public final class Perl5Pattern implements Pattern, Serializable, Cloneable {
    private static final long serialVersionUID = 2416464270803402709L;
    static final int _OPT_ANCH_BOL = 1;
    static final int _OPT_ANCH_MBOL = 2;
    static final int _OPT_SKIP = 4;
    static final int _OPT_IMPLICIT = 8;
    static final int _OPT_ANCH = 3;
    String _expression;
    char[] _program;
    int _mustUtility;
    int _back;
    int _minLength;
    int _numParentheses;
    boolean _isCaseInsensitive;
    boolean _isExpensive;
    int _startClassOffset;
    int _anchor;
    int _options;
    char[] _mustString;
    char[] _startString;

    Perl5Pattern() {
    }

    @Override // org.apache.oro.text.regex.Pattern
    public String getPattern() {
        return this._expression;
    }

    @Override // org.apache.oro.text.regex.Pattern
    public int getOptions() {
        return this._options;
    }
}
