package com.ctc.wstx.shaded.msv_core.datatype.regexp;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/ParseException.class */
class ParseException extends RuntimeException {
    int location;

    public ParseException(String mes, int location) {
        super(mes);
        this.location = location;
    }

    public int getLocation() {
        return this.location;
    }
}
