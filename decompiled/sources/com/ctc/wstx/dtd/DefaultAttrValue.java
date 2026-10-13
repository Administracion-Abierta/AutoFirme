package com.ctc.wstx.dtd;

import com.ctc.wstx.cfg.ErrorConsts;
import java.text.MessageFormat;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/DefaultAttrValue.class */
public final class DefaultAttrValue {
    public static final int DEF_DEFAULT = 1;
    public static final int DEF_IMPLIED = 2;
    public static final int DEF_REQUIRED = 3;
    public static final int DEF_FIXED = 4;
    static final DefaultAttrValue sImplied = new DefaultAttrValue(2);
    static final DefaultAttrValue sRequired = new DefaultAttrValue(3);
    final int mDefValueType;
    private String mValue = null;
    private UndeclaredEntity mUndeclaredEntity = null;

    private DefaultAttrValue(int defValueType) {
        this.mDefValueType = defValueType;
    }

    public static DefaultAttrValue constructImplied() {
        return sImplied;
    }

    public static DefaultAttrValue constructRequired() {
        return sRequired;
    }

    public static DefaultAttrValue constructFixed() {
        return new DefaultAttrValue(4);
    }

    public static DefaultAttrValue constructOptional() {
        return new DefaultAttrValue(1);
    }

    public void setValue(String v) {
        this.mValue = v;
    }

    public void addUndeclaredPE(String name, Location loc) {
        addUndeclaredEntity(name, loc, true);
    }

    public void addUndeclaredGE(String name, Location loc) {
        addUndeclaredEntity(name, loc, false);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void reportUndeclared(ValidationContext ctxt, XMLValidator dtd) throws XMLStreamException {
        this.mUndeclaredEntity.reportUndeclared(ctxt, dtd);
    }

    public boolean hasUndeclaredEntities() {
        return this.mUndeclaredEntity != null;
    }

    public String getValue() {
        return this.mValue;
    }

    public String getValueIfOk() {
        if (this.mUndeclaredEntity == null) {
            return this.mValue;
        }
        return null;
    }

    public boolean isRequired() {
        return this == sRequired;
    }

    public boolean isFixed() {
        return this.mDefValueType == 4;
    }

    public boolean hasDefaultValue() {
        return this.mDefValueType == 1 || this.mDefValueType == 4;
    }

    public boolean isSpecial() {
        return this != sImplied;
    }

    private void addUndeclaredEntity(String name, Location loc, boolean isPe) {
        if (this.mUndeclaredEntity == null) {
            this.mUndeclaredEntity = new UndeclaredEntity(name, loc, isPe);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/DefaultAttrValue$UndeclaredEntity.class */
    static final class UndeclaredEntity {
        final String mName;
        final boolean mIsPe;
        final Location mLocation;

        UndeclaredEntity(String name, Location loc, boolean isPe) {
            this.mName = name;
            this.mIsPe = isPe;
            this.mLocation = loc;
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
        public void reportUndeclared(ValidationContext ctxt, XMLValidator dtd) throws XMLStreamException {
            String str = ErrorConsts.ERR_DTD_UNDECLARED_ENTITY;
            Object[] objArr = new Object[2];
            objArr[0] = this.mIsPe ? "parsed" : "general";
            objArr[1] = this.mName;
            String msg = MessageFormat.format(str, objArr);
            XMLValidationProblem prob = new XMLValidationProblem(this.mLocation, msg, 3);
            prob.setReporter(dtd);
            ctxt.reportProblem(prob);
        }
    }
}
