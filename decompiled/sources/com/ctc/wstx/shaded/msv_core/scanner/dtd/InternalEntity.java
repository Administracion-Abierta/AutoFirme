package com.ctc.wstx.shaded.msv_core.scanner.dtd;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/InternalEntity.class */
class InternalEntity extends EntityDecl {
    char[] buf;

    InternalEntity(String name, char[] value) {
        this.name = name;
        this.buf = value;
    }
}
