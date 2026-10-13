package com.ctc.wstx.dtd;

import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.StringUtil;
import com.ctc.wstx.util.WordResolver;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/DTDAttribute.class */
public abstract class DTDAttribute {
    static final char CHAR_SPACE = ' ';
    public static final int TYPE_CDATA = 0;
    public static final int TYPE_ENUMERATED = 1;
    public static final int TYPE_ID = 2;
    public static final int TYPE_IDREF = 3;
    public static final int TYPE_IDREFS = 4;
    public static final int TYPE_ENTITY = 5;
    public static final int TYPE_ENTITIES = 6;
    public static final int TYPE_NOTATION = 7;
    public static final int TYPE_NMTOKEN = 8;
    public static final int TYPE_NMTOKENS = 9;
    static final String[] sTypes = {"CDATA", "ENUMERATED", DTDParser.TYPE_ID, DTDParser.TYPE_IDREF, DTDParser.TYPE_IDREFS, DTDParser.TYPE_ENTITY, DTDParser.TYPE_ENTITIES, DTDParser.TYPE_NOTATION, DTDParser.TYPE_NMTOKEN, DTDParser.TYPE_NMTOKENS};
    protected final PrefixedName mName;
    protected final int mSpecialIndex;
    protected final DefaultAttrValue mDefValue;
    protected final boolean mCfgNsAware;
    protected final boolean mCfgXml11;

    public abstract DTDAttribute cloneWith(int i);

    public abstract String validate(DTDValidatorBase dTDValidatorBase, char[] cArr, int i, int i2, boolean z) throws XMLStreamException;

    public abstract void validateDefault(InputProblemReporter inputProblemReporter, boolean z) throws XMLStreamException;

    public DTDAttribute(PrefixedName name, DefaultAttrValue defValue, int specIndex, boolean nsAware, boolean xml11) {
        this.mName = name;
        this.mDefValue = defValue;
        this.mSpecialIndex = specIndex;
        this.mCfgNsAware = nsAware;
        this.mCfgXml11 = xml11;
    }

    public final PrefixedName getName() {
        return this.mName;
    }

    public final String toString() {
        return this.mName.toString();
    }

    public final String getDefaultValue(ValidationContext ctxt, XMLValidator dtd) throws XMLStreamException {
        String val = this.mDefValue.getValueIfOk();
        if (val == null) {
            this.mDefValue.reportUndeclared(ctxt, dtd);
            val = this.mDefValue.getValue();
        }
        return val;
    }

    public final int getSpecialIndex() {
        return this.mSpecialIndex;
    }

    public final boolean needsValidation() {
        return getValueType() != 0;
    }

    public final boolean isFixed() {
        return this.mDefValue.isFixed();
    }

    public final boolean isRequired() {
        return this.mDefValue.isRequired();
    }

    public final boolean isSpecial() {
        return this.mDefValue.isSpecial();
    }

    public final boolean hasDefaultValue() {
        return this.mDefValue.hasDefaultValue();
    }

    public int getValueType() {
        return 0;
    }

    public String getValueTypeString() {
        return sTypes[getValueType()];
    }

    public boolean typeIsId() {
        return false;
    }

    public boolean typeIsNotation() {
        return false;
    }

    public String validate(DTDValidatorBase v, String value, boolean normalize) throws XMLStreamException {
        int len = value.length();
        char[] cbuf = v.getTempAttrValueBuffer(value.length());
        if (len > 0) {
            value.getChars(0, len, cbuf, 0);
        }
        return validate(v, cbuf, 0, len, normalize);
    }

    public String normalize(DTDValidatorBase v, char[] cbuf, int start, int end) {
        return StringUtil.normalizeSpaces(cbuf, start, end);
    }

    public void normalizeDefault() {
        char[] cbuf;
        String str;
        String val = this.mDefValue.getValue();
        if (val.length() > 0 && (str = StringUtil.normalizeSpaces((cbuf = val.toCharArray()), 0, cbuf.length)) != null) {
            this.mDefValue.setValue(str);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String validateDefaultName(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String origDefValue = this.mDefValue.getValue();
        String defValue = origDefValue.trim();
        if (defValue.length() == 0) {
            reportValidationProblem(rep, "Invalid default value '" + defValue + "'; empty String is not a valid name");
        }
        int illegalIx = WstxInputData.findIllegalNameChar(defValue, this.mCfgNsAware, this.mCfgXml11);
        if (illegalIx >= 0) {
            if (illegalIx == 0) {
                reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character " + WstxInputData.getCharDesc(defValue.charAt(0)) + ") not valid first character of a name");
            } else {
                reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character #" + illegalIx + " (" + WstxInputData.getCharDesc(defValue.charAt(illegalIx)) + ") not valid name character");
            }
        }
        return normalize ? defValue : origDefValue;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String validateDefaultNames(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        char c;
        String defValue = this.mDefValue.getValue().trim();
        int len = defValue.length();
        StringBuilder sb = null;
        int count = 0;
        int i = 0;
        loop0: while (true) {
            int start = i;
            if (start >= len) {
                break;
            }
            char cCharAt = defValue.charAt(start);
            while (true) {
                c = cCharAt;
                if (!WstxInputData.isSpaceChar(c)) {
                    break;
                }
                start++;
                if (start >= len) {
                    break loop0;
                }
                cCharAt = defValue.charAt(start);
            }
            int i2 = start + 1;
            while (i2 < len && !WstxInputData.isSpaceChar(defValue.charAt(i2))) {
                i2++;
            }
            String token = defValue.substring(start, i2);
            int illegalIx = WstxInputData.findIllegalNameChar(token, this.mCfgNsAware, this.mCfgXml11);
            if (illegalIx >= 0) {
                if (illegalIx == 0) {
                    reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character " + WstxInputData.getCharDesc(defValue.charAt(start)) + ") not valid first character of a name token");
                } else {
                    reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character " + WstxInputData.getCharDesc(c) + ") not a valid name character");
                }
            }
            count++;
            if (normalize) {
                if (sb == null) {
                    sb = new StringBuilder((i2 - start) + 32);
                } else {
                    sb.append(' ');
                }
                sb.append(token);
            }
            i = i2 + 1;
        }
        if (count == 0) {
            return reportValidationProblem(rep, "Invalid default value '" + defValue + "'; empty String is not a valid name value");
        }
        return sb != null ? sb.toString() : defValue;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String validateDefaultNmToken(InputProblemReporter rep, boolean normalize) throws XMLStreamException {
        String origDefValue = this.mDefValue.getValue();
        String defValue = origDefValue.trim();
        if (defValue.length() == 0) {
            reportValidationProblem(rep, "Invalid default value '" + defValue + "'; empty String is not a valid NMTOKEN");
        }
        int illegalIx = WstxInputData.findIllegalNmtokenChar(defValue, this.mCfgNsAware, this.mCfgXml11);
        if (illegalIx >= 0) {
            reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character #" + illegalIx + " (" + WstxInputData.getCharDesc(defValue.charAt(illegalIx)) + ") not valid NMTOKEN character");
        }
        return normalize ? defValue : origDefValue;
    }

    public String validateEnumValue(char[] cbuf, int start, int end, boolean normalize, WordResolver res) {
        if (normalize) {
            while (start < end && cbuf[start] <= ' ') {
                start++;
            }
            do {
                end--;
                if (end <= start) {
                    break;
                }
            } while (cbuf[end] <= ' ');
            end++;
        }
        if (start >= end) {
            return null;
        }
        return res.find(cbuf, start, end);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected EntityDecl findEntityDecl(DTDValidatorBase v, char[] ch, int start, int len) throws XMLStreamException {
        Map<String, EntityDecl> entMap = v.getEntityMap();
        String id = new String(ch, start, len);
        EntityDecl ent = entMap.get(id);
        if (ent == null) {
            reportValidationProblem(v, "Referenced entity '" + id + "' not defined");
        } else if (ent.isParsed()) {
            reportValidationProblem(v, "Referenced entity '" + id + "' is not an unparsed entity");
        }
        return ent;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void checkEntity(InputProblemReporter rep, String id, EntityDecl ent) throws XMLStreamException {
        if (ent == null) {
            rep.reportValidationProblem("Referenced entity '" + id + "' not defined");
        } else if (ent.isParsed()) {
            rep.reportValidationProblem("Referenced entity '" + id + "' is not an unparsed entity");
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String reportInvalidChar(DTDValidatorBase v, char c, String msg) throws XMLStreamException {
        reportValidationProblem(v, "Invalid character " + WstxInputData.getCharDesc(c) + ": " + msg);
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String reportValidationProblem(DTDValidatorBase v, String msg) throws XMLStreamException {
        v.reportValidationProblem("Attribute '" + this.mName + "': " + msg);
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String reportValidationProblem(InputProblemReporter rep, String msg) throws XMLStreamException {
        rep.reportValidationProblem("Attribute definition '" + this.mName + "': " + msg);
        return null;
    }
}
