package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import com.aowagie.text.ElementTags;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Image;
import com.aowagie.text.Rectangle;
import com.aowagie.text.pdf.codec.Base64;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/AcroFields.class */
public class AcroFields {
    PdfReader reader;
    PdfWriter writer;
    HashMap<String, Item> fields;
    private int topFirst;
    private HashMap sigNames;
    private boolean append;
    public static final int DA_FONT = 0;
    public static final int DA_SIZE = 1;
    public static final int DA_COLOR = 2;
    private XfaForm xfa;
    public static final int FIELD_TYPE_NONE = 0;
    public static final int FIELD_TYPE_PUSHBUTTON = 1;
    public static final int FIELD_TYPE_CHECKBOX = 2;
    public static final int FIELD_TYPE_RADIOBUTTON = 3;
    public static final int FIELD_TYPE_TEXT = 4;
    public static final int FIELD_TYPE_LIST = 5;
    public static final int FIELD_TYPE_COMBO = 6;
    public static final int FIELD_TYPE_SIGNATURE = 7;
    private static final int MAX_NUM_LEAPS_TO_FIND_PARENT = 5;
    private boolean lastWasString;
    private float extraMarginLeft;
    private float extraMarginTop;
    private ArrayList substitutionFonts;
    private static final HashMap stdFieldFontNames = new HashMap();
    private int totalRevisions;
    private Map fieldCache;
    private static final PdfName[] buttonRemove;
    private final HashMap extensionFonts = new HashMap();
    private boolean generateAppearances = true;
    private final HashMap localFonts = new HashMap();

    AcroFields(PdfReader reader, PdfWriter writer) {
        this.reader = reader;
        this.writer = writer;
        try {
            this.xfa = new XfaForm(reader);
            if (writer instanceof PdfStamperImp) {
                this.append = ((PdfStamperImp) writer).isAppend();
            }
            fill();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    void fill() {
        PdfArray arrfds;
        this.fields = new HashMap<>();
        PdfDictionary top = (PdfDictionary) PdfReader.getPdfObjectRelease(this.reader.getCatalog().get(PdfName.ACROFORM));
        if (top == null || (arrfds = (PdfArray) PdfReader.getPdfObjectRelease(top.get(PdfName.FIELDS))) == null || arrfds.size() == 0) {
            return;
        }
        for (int k = 1; k <= this.reader.getNumberOfPages(); k++) {
            PdfDictionary page = this.reader.getPageNRelease(k);
            PdfArray annots = (PdfArray) PdfReader.getPdfObjectRelease(page.get(PdfName.ANNOTS), page);
            if (annots != null) {
                for (int j = 0; j < annots.size(); j++) {
                    PdfDictionary annot = annots.getAsDict(j);
                    if (annot == null) {
                        PdfReader.releaseLastXrefPartial(annots.getAsIndirectObject(j));
                    } else {
                        boolean found = false;
                        PRIndirectReference foundSignRef = (PRIndirectReference) annots.getPdfObject(j);
                        PRIndirectReference parentFoundSignRef = getParentReference(annot);
                        for (int l = 0; l < arrfds.size() && !found; l++) {
                            PRIndirectReference declaredSignRef = (PRIndirectReference) arrfds.getPdfObject(l);
                            if (foundSignRef.getNumber() == declaredSignRef.getNumber()) {
                                found = true;
                            } else if (parentFoundSignRef != null && parentFoundSignRef.getNumber() == declaredSignRef.getNumber()) {
                                found = true;
                            }
                        }
                        if (!found) {
                            PdfReader.releaseLastXrefPartial(annots.getAsIndirectObject(j));
                        } else if (!PdfName.WIDGET.equals(annot.getAsName(PdfName.SUBTYPE))) {
                            PdfReader.releaseLastXrefPartial(annots.getAsIndirectObject(j));
                        } else if (!checkInvalidValuePosition(annot)) {
                            PdfReader.releaseLastXrefPartial(annots.getAsIndirectObject(j));
                        } else {
                            PdfDictionary dic = new PdfDictionary();
                            dic.putAll(annot);
                            String name = "";
                            PdfDictionary value = null;
                            PdfObject lastV = null;
                            while (annot != null) {
                                if (PdfName.SIG.equals(annot.getAsName(PdfName.FT)) && dic.get(PdfName.V) != null) {
                                    dic.remove(PdfName.V);
                                }
                                dic.mergeDifferent(annot);
                                PdfString t = annot.getAsString(PdfName.T);
                                if (t != null) {
                                    name = t.toUnicodeString() + "." + name;
                                }
                                if (lastV == null && annot.get(PdfName.V) != null) {
                                    lastV = PdfReader.getPdfObjectRelease(annot.get(PdfName.V));
                                }
                                if (value == null && t != null) {
                                    value = annot;
                                    if (annot.get(PdfName.V) == null && lastV != null) {
                                        value.put(PdfName.V, lastV);
                                    }
                                }
                                annot = annot.getAsDict(PdfName.PARENT);
                            }
                            if (name.length() > 0) {
                                name = name.substring(0, name.length() - 1);
                            }
                            Item item = this.fields.get(name);
                            if (item == null) {
                                item = new Item();
                                this.fields.put(name, item);
                            }
                            if (value == null) {
                                item.addValue(annot);
                            } else {
                                item.addValue(value);
                            }
                            item.addWidget(annot);
                            item.addWidgetRef(annots.getAsIndirectObject(j));
                            if (top != null) {
                                dic.mergeDifferent(top);
                            }
                            item.addMerged(dic);
                            item.addPage(k);
                            item.addTabOrder(j);
                        }
                    }
                }
            }
        }
        PdfNumber sigFlags = top != null ? top.getAsNumber(PdfName.SIGFLAGS) : null;
        if (sigFlags == null || (sigFlags.intValue() & 1) != 1) {
            return;
        }
        for (int j2 = 0; j2 < arrfds.size(); j2++) {
            PdfDictionary annot2 = arrfds.getAsDict(j2);
            if (annot2 == null) {
                PdfReader.releaseLastXrefPartial(arrfds.getAsIndirectObject(j2));
            } else if (!PdfName.WIDGET.equals(annot2.getAsName(PdfName.SUBTYPE))) {
                PdfReader.releaseLastXrefPartial(arrfds.getAsIndirectObject(j2));
            } else {
                PdfArray kids = (PdfArray) PdfReader.getPdfObjectRelease(annot2.get(PdfName.KIDS));
                if (kids == null) {
                    PdfDictionary dic2 = new PdfDictionary();
                    dic2.putAll(annot2);
                    PdfString t2 = annot2.getAsString(PdfName.T);
                    if (t2 != null) {
                        String name2 = t2.toUnicodeString();
                        if (!this.fields.containsKey(name2)) {
                            Item item2 = new Item();
                            this.fields.put(name2, item2);
                            item2.addValue(dic2);
                            item2.addWidget(dic2);
                            item2.addWidgetRef(arrfds.getAsIndirectObject(j2));
                            item2.addMerged(dic2);
                            item2.addPage(-1);
                            item2.addTabOrder(-1);
                        }
                    }
                }
            }
        }
    }

    private static boolean checkInvalidValuePosition(PdfDictionary targetAnnot) {
        PdfDictionary annot = new PdfDictionary();
        annot.putAll(targetAnnot);
        PdfDictionary dic = new PdfDictionary();
        dic.putAll(annot);
        while (annot != null) {
            if (PdfName.SIG.equals(annot.getAsName(PdfName.FT)) && dic.get(PdfName.V) != null) {
                if (annot.get(PdfName.V) == null) {
                    return false;
                }
                dic.remove(PdfName.V);
            }
            dic.mergeDifferent(annot);
            annot = annot.getAsDict(PdfName.PARENT);
        }
        return true;
    }

    private static PRIndirectReference getParentReference(PdfDictionary dict) {
        PdfDictionary parentDict = dict;
        int leap = 0;
        PRIndirectReference parentRef = null;
        do {
            PdfObject parentObj = parentDict.get(PdfName.PARENT);
            if (parentObj != null && (parentObj instanceof PRIndirectReference)) {
                parentRef = (PRIndirectReference) parentObj;
            }
            parentDict = parentDict.getAsDict(PdfName.PARENT);
            leap++;
            if (parentDict == null) {
                break;
            }
        } while (leap < 5);
        return parentRef;
    }

    public String[] getAppearanceStates(String fieldName) {
        PdfDictionary dic;
        Item fd = this.fields.get(fieldName);
        if (fd == null) {
            return null;
        }
        HashMap names = new HashMap();
        PdfDictionary vals = fd.getValue(0);
        PdfString stringOpt = vals.getAsString(PdfName.OPT);
        if (stringOpt != null) {
            names.put(stringOpt.toUnicodeString(), null);
        } else {
            PdfArray arrayOpt = vals.getAsArray(PdfName.OPT);
            if (arrayOpt != null) {
                for (int k = 0; k < arrayOpt.size(); k++) {
                    PdfString valStr = arrayOpt.getAsString(k);
                    if (valStr != null) {
                        names.put(valStr.toUnicodeString(), null);
                    }
                }
            }
        }
        for (int k2 = 0; k2 < fd.size(); k2++) {
            PdfDictionary dic2 = fd.getWidget(k2).getAsDict(PdfName.AP);
            if (dic2 != null && (dic = dic2.getAsDict(PdfName.N)) != null) {
                for (Object element : dic.getKeys()) {
                    String name = PdfName.decodeName(((PdfName) element).toString());
                    names.put(name, null);
                }
            }
        }
        String[] out = new String[names.size()];
        return (String[]) names.keySet().toArray(out);
    }

    private String[] getListOption(String fieldName, int idx) {
        PdfArray ar;
        Item fd = getFieldItem(fieldName);
        if (fd == null || (ar = fd.getMerged(0).getAsArray(PdfName.OPT)) == null) {
            return null;
        }
        String[] ret = new String[ar.size()];
        for (int k = 0; k < ar.size(); k++) {
            PdfObject obj = ar.getDirectObject(k);
            try {
                if (obj.isArray()) {
                    obj = ((PdfArray) obj).getDirectObject(idx);
                }
                if (obj.isString()) {
                    ret[k] = ((PdfString) obj).toUnicodeString();
                } else {
                    ret[k] = obj.toString();
                }
            } catch (Exception e) {
                ret[k] = "";
            }
        }
        return ret;
    }

    public String[] getListOptionExport(String fieldName) {
        return getListOption(fieldName, 0);
    }

    public String[] getListOptionDisplay(String fieldName) {
        return getListOption(fieldName, 1);
    }

    public boolean setListOption(String fieldName, String[] exportValues, String[] displayValues) {
        if (exportValues == null && displayValues == null) {
            return false;
        }
        if (exportValues != null && displayValues != null && exportValues.length != displayValues.length) {
            throw new IllegalArgumentException("The export and the display array must have the same size.");
        }
        int ftype = getFieldType(fieldName);
        if (ftype != 6 && ftype != 5) {
            return false;
        }
        Item item = this.fields.get(fieldName);
        String[] sing = null;
        if (exportValues == null && displayValues != null) {
            sing = displayValues;
        } else if (exportValues != null && displayValues == null) {
            sing = exportValues;
        }
        PdfArray opt = new PdfArray();
        if (sing != null) {
            for (String element : sing) {
                opt.add(new PdfString(element, PdfObject.TEXT_UNICODE));
            }
        } else {
            for (int k = 0; k < exportValues.length; k++) {
                PdfArray a = new PdfArray();
                a.add(new PdfString(exportValues[k], PdfObject.TEXT_UNICODE));
                a.add(new PdfString(displayValues[k], PdfObject.TEXT_UNICODE));
                opt.add(a);
            }
        }
        item.writeToAll(PdfName.OPT, opt, 5);
        return true;
    }

    public int getFieldType(String fieldName) {
        PdfDictionary merged;
        PdfName type;
        Item fd = getFieldItem(fieldName);
        if (fd == null || (type = (merged = fd.getMerged(0)).getAsName(PdfName.FT)) == null) {
            return 0;
        }
        int ff = 0;
        PdfNumber ffo = merged.getAsNumber(PdfName.FF);
        if (ffo != null) {
            ff = ffo.intValue();
        }
        if (PdfName.BTN.equals(type)) {
            if ((ff & 65536) != 0) {
                return 1;
            }
            if ((ff & 32768) != 0) {
                return 3;
            }
            return 2;
        }
        if (PdfName.TX.equals(type)) {
            return 4;
        }
        if (PdfName.CH.equals(type)) {
            if ((ff & 131072) != 0) {
                return 6;
            }
            return 5;
        }
        if (PdfName.SIG.equals(type)) {
            return 7;
        }
        return 0;
    }

    public void exportAsFdf(FdfWriter writer) {
        for (Object element : this.fields.entrySet()) {
            Map.Entry<String, Item> entry = (Map.Entry) element;
            Item item = entry.getValue();
            String name = entry.getKey();
            PdfObject v = item.getMerged(0).get(PdfName.V);
            if (v != null) {
                String value = getField(name);
                if (this.lastWasString) {
                    writer.setFieldAsString(name, value);
                } else {
                    writer.setFieldAsName(name, value);
                }
            }
        }
    }

    public boolean renameField(String oldName, String newName) {
        Item item;
        int idx1 = oldName.lastIndexOf(46) + 1;
        int idx2 = newName.lastIndexOf(46) + 1;
        if (idx1 != idx2 || !oldName.substring(0, idx1).equals(newName.substring(0, idx2)) || this.fields.containsKey(newName) || (item = this.fields.get(oldName)) == null) {
            return false;
        }
        String newName2 = newName.substring(idx2);
        PdfString ss = new PdfString(newName2, PdfObject.TEXT_UNICODE);
        item.writeToAll(PdfName.T, ss, 5);
        item.markUsed(this, 4);
        this.fields.remove(oldName);
        this.fields.put(newName2, item);
        return true;
    }

    public static Object[] splitDAelements(String da) {
        try {
            PRTokeniser tk = new PRTokeniser(PdfEncodings.convertToBytes(da, (String) null));
            ArrayList stack = new ArrayList();
            Object[] ret = new Object[3];
            while (tk.nextToken()) {
                if (tk.getTokenType() != 4) {
                    if (tk.getTokenType() == 10) {
                        String operator = tk.getStringValue();
                        if (operator.equals(OperatorName.SET_FONT_AND_SIZE)) {
                            if (stack.size() >= 2) {
                                ret[0] = stack.get(stack.size() - 2);
                                ret[1] = new Float((String) stack.get(stack.size() - 1));
                            }
                        } else if (operator.equals(OperatorName.NON_STROKING_GRAY)) {
                            if (stack.size() >= 1) {
                                float gray = new Float((String) stack.get(stack.size() - 1)).floatValue();
                                if (gray != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                                    ret[2] = new GrayColor(gray);
                                }
                            }
                        } else if (operator.equals(OperatorName.NON_STROKING_RGB)) {
                            if (stack.size() >= 3) {
                                float red = new Float((String) stack.get(stack.size() - 3)).floatValue();
                                float green = new Float((String) stack.get(stack.size() - 2)).floatValue();
                                float blue = new Float((String) stack.get(stack.size() - 1)).floatValue();
                                ret[2] = new Color(red, green, blue);
                            }
                        } else if (operator.equals(OperatorName.NON_STROKING_CMYK) && stack.size() >= 4) {
                            float cyan = new Float((String) stack.get(stack.size() - 4)).floatValue();
                            float magenta = new Float((String) stack.get(stack.size() - 3)).floatValue();
                            float yellow = new Float((String) stack.get(stack.size() - 2)).floatValue();
                            float black = new Float((String) stack.get(stack.size() - 1)).floatValue();
                            ret[2] = new CMYKColor(cyan, magenta, yellow, black);
                        }
                        stack.clear();
                    } else {
                        stack.add(tk.getStringValue());
                    }
                }
            }
            return ret;
        } catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    public void decodeGenericDictionary(PdfDictionary merged, BaseField tx) throws IOException, DocumentException {
        PdfDictionary font;
        PdfDictionary font2;
        PdfString da = merged.getAsString(PdfName.DA);
        if (da != null) {
            Object[] dab = splitDAelements(da.toUnicodeString());
            if (dab[1] != null) {
                tx.setFontSize(((Float) dab[1]).floatValue());
            }
            if (dab[2] != null) {
                tx.setTextColor((Color) dab[2]);
            }
            if (dab[0] != null && (font = merged.getAsDict(PdfName.DR)) != null && (font2 = font.getAsDict(PdfName.FONT)) != null) {
                PdfObject po = font2.get(new PdfName((String) dab[0]));
                if (po != null && po.type() == 10) {
                    PRIndirectReference por = (PRIndirectReference) po;
                    BaseFont bp = new DocumentFont((PRIndirectReference) po);
                    tx.setFont(bp);
                    Integer porkey = new Integer(por.getNumber());
                    BaseFont porf = (BaseFont) this.extensionFonts.get(porkey);
                    if (porf == null && !this.extensionFonts.containsKey(porkey)) {
                        PdfDictionary fo = (PdfDictionary) PdfReader.getPdfObject(po);
                        PdfDictionary fd = fo.getAsDict(PdfName.FONTDESCRIPTOR);
                        if (fd != null) {
                            PRStream prs = (PRStream) PdfReader.getPdfObject(fd.get(PdfName.FONTFILE2));
                            if (prs == null) {
                                prs = (PRStream) PdfReader.getPdfObject(fd.get(PdfName.FONTFILE3));
                            }
                            if (prs == null) {
                                this.extensionFonts.put(porkey, null);
                            } else {
                                try {
                                    porf = BaseFont.createFont("font.ttf", BaseFont.IDENTITY_H, true, false, PdfReader.getStreamBytes(prs), null);
                                } catch (Exception e) {
                                }
                                this.extensionFonts.put(porkey, porf);
                            }
                        }
                    }
                    if (tx instanceof TextField) {
                        ((TextField) tx).setExtensionFont(porf);
                    }
                } else {
                    BaseFont bf = (BaseFont) this.localFonts.get(dab[0]);
                    if (bf == null) {
                        String[] fn = (String[]) stdFieldFontNames.get(dab[0]);
                        if (fn != null) {
                            try {
                                String enc = "winansi";
                                if (fn.length > 1) {
                                    enc = fn[1];
                                }
                                tx.setFont(BaseFont.createFont(fn[0], enc, false));
                            } catch (Exception e2) {
                            }
                        }
                    } else {
                        tx.setFont(bf);
                    }
                }
            }
        }
        PdfDictionary mk = merged.getAsDict(PdfName.MK);
        if (mk != null) {
            PdfArray ar = mk.getAsArray(PdfName.BC);
            Color border = getMKColor(ar);
            tx.setBorderColor(border);
            if (border != null) {
                tx.setBorderWidth(1.0f);
            }
            PdfArray ar2 = mk.getAsArray(PdfName.BG);
            tx.setBackgroundColor(getMKColor(ar2));
            PdfNumber rotation = mk.getAsNumber(PdfName.R);
            if (rotation != null) {
                tx.setRotation(rotation.intValue());
            }
        }
        PdfNumber nfl = merged.getAsNumber(PdfName.F);
        tx.setVisibility(2);
        if (nfl != null) {
            int flags = nfl.intValue();
            if ((flags & 4) != 0 && (flags & 2) != 0) {
                tx.setVisibility(1);
            } else if ((flags & 4) != 0 && (flags & 32) != 0) {
                tx.setVisibility(3);
            } else if ((flags & 4) != 0) {
                tx.setVisibility(0);
            }
        }
        PdfNumber nfl2 = merged.getAsNumber(PdfName.FF);
        int flags2 = 0;
        if (nfl2 != null) {
            flags2 = nfl2.intValue();
        }
        tx.setOptions(flags2);
        if ((flags2 & 16777216) != 0) {
            PdfNumber maxLen = merged.getAsNumber(PdfName.MAXLEN);
            int len = 0;
            if (maxLen != null) {
                len = maxLen.intValue();
            }
            tx.setMaxCharacterLength(len);
        }
        PdfNumber nfl3 = merged.getAsNumber(PdfName.Q);
        if (nfl3 != null) {
            if (nfl3.intValue() == 1) {
                tx.setAlignment(1);
            } else if (nfl3.intValue() == 2) {
                tx.setAlignment(2);
            }
        }
        PdfDictionary bs = merged.getAsDict(PdfName.BS);
        if (bs != null) {
            PdfNumber w = bs.getAsNumber(PdfName.W);
            if (w != null) {
                tx.setBorderWidth(w.floatValue());
            }
            PdfName s = bs.getAsName(PdfName.S);
            if (PdfName.D.equals(s)) {
                tx.setBorderStyle(1);
                return;
            }
            if (PdfName.B.equals(s)) {
                tx.setBorderStyle(2);
                return;
            } else if (PdfName.I.equals(s)) {
                tx.setBorderStyle(3);
                return;
            } else {
                if (PdfName.U.equals(s)) {
                    tx.setBorderStyle(4);
                    return;
                }
                return;
            }
        }
        PdfArray bd = merged.getAsArray(PdfName.BORDER);
        if (bd != null) {
            if (bd.size() >= 3) {
                tx.setBorderWidth(bd.getAsNumber(2).floatValue());
            }
            if (bd.size() >= 4) {
                tx.setBorderStyle(1);
            }
        }
    }

    PdfAppearance getAppearance(PdfDictionary merged, String text, String fieldName) throws IOException, DocumentException {
        TextField tx;
        this.topFirst = 0;
        if (this.fieldCache == null || !this.fieldCache.containsKey(fieldName)) {
            tx = new TextField(this.writer, null, null);
            tx.setExtraMargin(this.extraMarginLeft, this.extraMarginTop);
            tx.setBorderWidth(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            tx.setSubstitutionFonts(this.substitutionFonts);
            decodeGenericDictionary(merged, tx);
            PdfArray rect = merged.getAsArray(PdfName.RECT);
            Rectangle box = PdfReader.getNormalizedRectangle(rect);
            if (tx.getRotation() == 90 || tx.getRotation() == 270) {
                box = box.rotate();
            }
            tx.setBox(box);
            if (this.fieldCache != null) {
                this.fieldCache.put(fieldName, tx);
            }
        } else {
            tx = (TextField) this.fieldCache.get(fieldName);
            tx.setWriter(this.writer);
        }
        PdfName fieldType = merged.getAsName(PdfName.FT);
        if (PdfName.TX.equals(fieldType)) {
            tx.setText(text);
            return tx.getAppearance();
        }
        if (!PdfName.CH.equals(fieldType)) {
            throw new DocumentException("An appearance was requested without a variable text field.");
        }
        PdfArray opt = merged.getAsArray(PdfName.OPT);
        int flags = 0;
        PdfNumber nfl = merged.getAsNumber(PdfName.FF);
        if (nfl != null) {
            flags = nfl.intValue();
        }
        if ((flags & 131072) != 0 && opt == null) {
            tx.setText(text);
            return tx.getAppearance();
        }
        if (opt != null) {
            String[] choices = new String[opt.size()];
            String[] choicesExp = new String[opt.size()];
            for (int k = 0; k < opt.size(); k++) {
                PdfObject obj = opt.getPdfObject(k);
                if (obj.isString()) {
                    String unicodeString = ((PdfString) obj).toUnicodeString();
                    choicesExp[k] = unicodeString;
                    choices[k] = unicodeString;
                } else {
                    PdfArray a = (PdfArray) obj;
                    choicesExp[k] = a.getAsString(0).toUnicodeString();
                    choices[k] = a.getAsString(1).toUnicodeString();
                }
            }
            if ((flags & 131072) != 0) {
                for (int k2 = 0; k2 < choices.length; k2++) {
                    if (text.equals(choicesExp[k2])) {
                        text = choices[k2];
                        break;
                    }
                }
                tx.setText(text);
                return tx.getAppearance();
            }
            int idx = 0;
            for (int k3 = 0; k3 < choicesExp.length; k3++) {
                if (text.equals(choicesExp[k3])) {
                    idx = k3;
                    break;
                }
            }
            tx.setChoices(choices);
            tx.setChoiceExports(choicesExp);
            tx.setChoiceSelection(idx);
        }
        PdfAppearance app = tx.getListAppearance();
        this.topFirst = tx.getTopFirst();
        return app;
    }

    Color getMKColor(PdfArray ar) {
        if (ar == null) {
            return null;
        }
        switch (ar.size()) {
            case 1:
                return new GrayColor(ar.getAsNumber(0).floatValue());
            case 2:
            default:
                return null;
            case 3:
                return new Color(ExtendedColor.normalize(ar.getAsNumber(0).floatValue()), ExtendedColor.normalize(ar.getAsNumber(1).floatValue()), ExtendedColor.normalize(ar.getAsNumber(2).floatValue()));
            case 4:
                return new CMYKColor(ar.getAsNumber(0).floatValue(), ar.getAsNumber(1).floatValue(), ar.getAsNumber(2).floatValue(), ar.getAsNumber(3).floatValue());
        }
    }

    public String getField(String name) {
        if (this.xfa.isXfaPresent()) {
            String name2 = this.xfa.findFieldName(name, this);
            if (name2 == null) {
                return null;
            }
            return XfaForm.getNodeText(this.xfa.findDatasetsNode(XfaForm.Xml2Som.getShortName(name2)));
        }
        Item item = this.fields.get(name);
        if (item == null) {
            return null;
        }
        this.lastWasString = false;
        PdfDictionary mergedDict = item.getMerged(0);
        PdfObject v = PdfReader.getPdfObject(mergedDict.get(PdfName.V));
        if (v == null) {
            return "";
        }
        if (v instanceof PRStream) {
            try {
                byte[] valBytes = PdfReader.getStreamBytes((PRStream) v);
                return new String(valBytes);
            } catch (IOException e) {
                throw new ExceptionConverter(e);
            }
        }
        PdfName type = mergedDict.getAsName(PdfName.FT);
        if (PdfName.BTN.equals(type)) {
            PdfNumber ff = mergedDict.getAsNumber(PdfName.FF);
            int flags = 0;
            if (ff != null) {
                flags = ff.intValue();
            }
            if ((flags & 65536) != 0) {
                return "";
            }
            String value = "";
            if (v instanceof PdfName) {
                value = PdfName.decodeName(v.toString());
            } else if (v instanceof PdfString) {
                value = ((PdfString) v).toUnicodeString();
            }
            PdfArray opts = item.getValue(0).getAsArray(PdfName.OPT);
            if (opts != null) {
                try {
                    int idx = Integer.parseInt(value);
                    PdfString ps = opts.getAsString(idx);
                    value = ps.toUnicodeString();
                    this.lastWasString = true;
                } catch (Exception e2) {
                }
            }
            return value;
        }
        if (v instanceof PdfString) {
            this.lastWasString = true;
            return ((PdfString) v).toUnicodeString();
        }
        if (v instanceof PdfName) {
            return PdfName.decodeName(v.toString());
        }
        return "";
    }

    public String[] getListSelection(String name) {
        String[] ret;
        String s = getField(name);
        if (s == null) {
            ret = new String[0];
        } else {
            ret = new String[]{s};
        }
        Item item = this.fields.get(name);
        if (item == null) {
            return ret;
        }
        PdfArray values = item.getMerged(0).getAsArray(PdfName.I);
        if (values == null) {
            return ret;
        }
        String[] ret2 = new String[values.size()];
        String[] options = getListOptionExport(name);
        int idx = 0;
        Iterator i = values.listIterator();
        while (i.hasNext()) {
            PdfNumber n = (PdfNumber) i.next();
            int i2 = idx;
            idx++;
            ret2[i2] = options[n.intValue()];
        }
        return ret2;
    }

    public boolean setFieldProperty(String field, String name, Object value, int[] inst) {
        FontDetails fd;
        if (this.writer == null) {
            throw new RuntimeException("This AcroFields instance is read-only.");
        }
        try {
            Item item = this.fields.get(field);
            if (item == null) {
                return false;
            }
            InstHit hit = new InstHit(inst);
            if (name.equalsIgnoreCase("textfont")) {
                for (int k = 0; k < item.size(); k++) {
                    if (hit.isHit(k)) {
                        PdfDictionary merged = item.getMerged(k);
                        PdfString da = merged.getAsString(PdfName.DA);
                        PdfDictionary dr = merged.getAsDict(PdfName.DR);
                        if (da != null && dr != null) {
                            Object[] dao = splitDAelements(da.toUnicodeString());
                            PdfAppearance cb = new PdfAppearance();
                            if (dao[0] != null) {
                                BaseFont bf = (BaseFont) value;
                                PdfName psn = (PdfName) PdfAppearance.stdFieldFontNames.get(bf.getPostscriptFontName());
                                if (psn == null) {
                                    psn = new PdfName(bf.getPostscriptFontName());
                                }
                                PdfDictionary fonts = dr.getAsDict(PdfName.FONT);
                                if (fonts == null) {
                                    fonts = new PdfDictionary();
                                    dr.put(PdfName.FONT, fonts);
                                }
                                PdfIndirectReference fref = (PdfIndirectReference) fonts.get(psn);
                                PdfDictionary top = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
                                markUsed(top);
                                PdfDictionary dr2 = top.getAsDict(PdfName.DR);
                                if (dr2 == null) {
                                    dr2 = new PdfDictionary();
                                    top.put(PdfName.DR, dr2);
                                }
                                markUsed(dr2);
                                PdfDictionary fontsTop = dr2.getAsDict(PdfName.FONT);
                                if (fontsTop == null) {
                                    fontsTop = new PdfDictionary();
                                    dr2.put(PdfName.FONT, fontsTop);
                                }
                                markUsed(fontsTop);
                                PdfIndirectReference frefTop = (PdfIndirectReference) fontsTop.get(psn);
                                if (frefTop != null) {
                                    if (fref == null) {
                                        fonts.put(psn, frefTop);
                                    }
                                } else if (fref == null) {
                                    if (bf.getFontType() == 4) {
                                        fd = new FontDetails(null, ((DocumentFont) bf).getIndirectReference(), bf);
                                    } else {
                                        bf.setSubset(false);
                                        fd = this.writer.addSimple(bf);
                                        this.localFonts.put(psn.toString().substring(1), bf);
                                    }
                                    fontsTop.put(psn, fd.getIndirectReference());
                                    fonts.put(psn, fd.getIndirectReference());
                                }
                                ByteBuffer buf = cb.getInternalBuffer();
                                buf.append(psn.getBytes()).append(' ').append(((Float) dao[1]).floatValue()).append(" Tf ");
                                if (dao[2] != null) {
                                    cb.setColorFill((Color) dao[2]);
                                }
                                PdfString s = new PdfString(cb.toString());
                                item.getMerged(k).put(PdfName.DA, s);
                                item.getWidget(k).put(PdfName.DA, s);
                                markUsed(item.getWidget(k));
                            }
                        }
                    }
                }
                return true;
            }
            if (name.equalsIgnoreCase("textcolor")) {
                for (int k2 = 0; k2 < item.size(); k2++) {
                    if (hit.isHit(k2)) {
                        PdfDictionary merged2 = item.getMerged(k2);
                        PdfString da2 = merged2.getAsString(PdfName.DA);
                        if (da2 != null) {
                            Object[] dao2 = splitDAelements(da2.toUnicodeString());
                            PdfAppearance cb2 = new PdfAppearance();
                            if (dao2[0] != null) {
                                ByteBuffer buf2 = cb2.getInternalBuffer();
                                buf2.append(new PdfName((String) dao2[0]).getBytes()).append(' ').append(((Float) dao2[1]).floatValue()).append(" Tf ");
                                cb2.setColorFill((Color) value);
                                PdfString s2 = new PdfString(cb2.toString());
                                item.getMerged(k2).put(PdfName.DA, s2);
                                item.getWidget(k2).put(PdfName.DA, s2);
                                markUsed(item.getWidget(k2));
                            }
                        }
                    }
                }
                return true;
            }
            if (name.equalsIgnoreCase("textsize")) {
                for (int k3 = 0; k3 < item.size(); k3++) {
                    if (hit.isHit(k3)) {
                        PdfDictionary merged3 = item.getMerged(k3);
                        PdfString da3 = merged3.getAsString(PdfName.DA);
                        if (da3 != null) {
                            Object[] dao3 = splitDAelements(da3.toUnicodeString());
                            PdfAppearance cb3 = new PdfAppearance();
                            if (dao3[0] != null) {
                                ByteBuffer buf3 = cb3.getInternalBuffer();
                                buf3.append(new PdfName((String) dao3[0]).getBytes()).append(' ').append(((Float) value).floatValue()).append(" Tf ");
                                if (dao3[2] != null) {
                                    cb3.setColorFill((Color) dao3[2]);
                                }
                                PdfString s3 = new PdfString(cb3.toString());
                                item.getMerged(k3).put(PdfName.DA, s3);
                                item.getWidget(k3).put(PdfName.DA, s3);
                                markUsed(item.getWidget(k3));
                            }
                        }
                    }
                }
                return true;
            }
            if (name.equalsIgnoreCase("bgcolor") || name.equalsIgnoreCase(ElementTags.BORDERCOLOR)) {
                PdfName dname = name.equalsIgnoreCase("bgcolor") ? PdfName.BG : PdfName.BC;
                for (int k4 = 0; k4 < item.size(); k4++) {
                    if (hit.isHit(k4)) {
                        PdfDictionary merged4 = item.getMerged(k4);
                        PdfDictionary mk = merged4.getAsDict(PdfName.MK);
                        if (mk == null) {
                            if (value == null) {
                                return true;
                            }
                            mk = new PdfDictionary();
                            item.getMerged(k4).put(PdfName.MK, mk);
                            item.getWidget(k4).put(PdfName.MK, mk);
                            markUsed(item.getWidget(k4));
                        } else {
                            markUsed(mk);
                        }
                        if (value == null) {
                            mk.remove(dname);
                        } else {
                            mk.put(dname, PdfAnnotation.getMKColor((Color) value));
                        }
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public boolean setFieldProperty(String field, String name, int value, int[] inst) {
        if (this.writer == null) {
            throw new RuntimeException("This AcroFields instance is read-only.");
        }
        Item item = this.fields.get(field);
        if (item == null) {
            return false;
        }
        InstHit hit = new InstHit(inst);
        if (name.equalsIgnoreCase("flags")) {
            PdfNumber num = new PdfNumber(value);
            for (int k = 0; k < item.size(); k++) {
                if (hit.isHit(k)) {
                    item.getMerged(k).put(PdfName.F, num);
                    item.getWidget(k).put(PdfName.F, num);
                    markUsed(item.getWidget(k));
                }
            }
            return true;
        }
        if (name.equalsIgnoreCase("setflags")) {
            for (int k2 = 0; k2 < item.size(); k2++) {
                if (hit.isHit(k2)) {
                    PdfNumber num2 = item.getWidget(k2).getAsNumber(PdfName.F);
                    int val = 0;
                    if (num2 != null) {
                        val = num2.intValue();
                    }
                    PdfNumber num3 = new PdfNumber(val | value);
                    item.getMerged(k2).put(PdfName.F, num3);
                    item.getWidget(k2).put(PdfName.F, num3);
                    markUsed(item.getWidget(k2));
                }
            }
            return true;
        }
        if (name.equalsIgnoreCase("clrflags")) {
            for (int k3 = 0; k3 < item.size(); k3++) {
                if (hit.isHit(k3)) {
                    PdfDictionary widget = item.getWidget(k3);
                    PdfNumber num4 = widget.getAsNumber(PdfName.F);
                    int val2 = 0;
                    if (num4 != null) {
                        val2 = num4.intValue();
                    }
                    PdfNumber num5 = new PdfNumber(val2 & (value ^ (-1)));
                    item.getMerged(k3).put(PdfName.F, num5);
                    widget.put(PdfName.F, num5);
                    markUsed(widget);
                }
            }
            return true;
        }
        if (name.equalsIgnoreCase("fflags")) {
            PdfNumber num6 = new PdfNumber(value);
            for (int k4 = 0; k4 < item.size(); k4++) {
                if (hit.isHit(k4)) {
                    item.getMerged(k4).put(PdfName.FF, num6);
                    item.getValue(k4).put(PdfName.FF, num6);
                    markUsed(item.getValue(k4));
                }
            }
            return true;
        }
        if (name.equalsIgnoreCase("setfflags")) {
            for (int k5 = 0; k5 < item.size(); k5++) {
                if (hit.isHit(k5)) {
                    PdfDictionary valDict = item.getValue(k5);
                    PdfNumber num7 = valDict.getAsNumber(PdfName.FF);
                    int val3 = 0;
                    if (num7 != null) {
                        val3 = num7.intValue();
                    }
                    PdfNumber num8 = new PdfNumber(val3 | value);
                    item.getMerged(k5).put(PdfName.FF, num8);
                    valDict.put(PdfName.FF, num8);
                    markUsed(valDict);
                }
            }
            return true;
        }
        if (name.equalsIgnoreCase("clrfflags")) {
            for (int k6 = 0; k6 < item.size(); k6++) {
                if (hit.isHit(k6)) {
                    PdfDictionary valDict2 = item.getValue(k6);
                    PdfNumber num9 = valDict2.getAsNumber(PdfName.FF);
                    int val4 = 0;
                    if (num9 != null) {
                        val4 = num9.intValue();
                    }
                    PdfNumber num10 = new PdfNumber(val4 & (value ^ (-1)));
                    item.getMerged(k6).put(PdfName.FF, num10);
                    valDict2.put(PdfName.FF, num10);
                    markUsed(valDict2);
                }
            }
            return true;
        }
        return false;
    }

    public void mergeXfaData(Node n) throws IOException, DocumentException {
        XfaForm.Xml2SomDatasets data = new XfaForm.Xml2SomDatasets(n);
        for (String name : data.getOrder()) {
            String text = XfaForm.getNodeText((Node) data.getName2Node().get(name));
            setField(name, text);
        }
    }

    public void setFields(FdfReader fdf) throws IOException, DocumentException {
        HashMap fd = fdf.getFields();
        for (String f : fd.keySet()) {
            String v = fdf.getFieldValue(f);
            if (v != null) {
                setField(f, v);
            }
        }
    }

    public void setFields(XfdfReader xfdf) throws IOException, DocumentException {
        HashMap fd = xfdf.getFields();
        for (String f : fd.keySet()) {
            String v = xfdf.getFieldValue(f);
            if (v != null) {
                setField(f, v);
            }
            List l = xfdf.getListValues(f);
            if (l != null) {
                setListSelection(v, (String[]) l.toArray(new String[l.size()]));
            }
        }
    }

    public boolean regenerateField(String name) throws IOException, DocumentException {
        String value = getField(name);
        return setField(name, value, value);
    }

    public boolean setField(String name, String value) throws IOException, DocumentException {
        return setField(name, value, null);
    }

    public boolean setField(String name, String value, String display) throws IOException, DocumentException {
        PdfName vt;
        if (this.writer == null) {
            throw new DocumentException("This AcroFields instance is read-only.");
        }
        if (this.xfa.isXfaPresent()) {
            name = this.xfa.findFieldName(name, this);
            if (name == null) {
                return false;
            }
            String shortName = XfaForm.Xml2Som.getShortName(name);
            Node xn = this.xfa.findDatasetsNode(shortName);
            if (xn == null) {
                xn = this.xfa.getDatasetsSom().insertNode(this.xfa.getDatasetsNode(), shortName);
            }
            this.xfa.setNodeText(xn, value);
        }
        Item item = this.fields.get(name);
        if (item == null) {
            return false;
        }
        PdfDictionary merged = item.getMerged(0);
        PdfName type = merged.getAsName(PdfName.FT);
        if (PdfName.TX.equals(type)) {
            PdfNumber maxLen = merged.getAsNumber(PdfName.MAXLEN);
            int len = 0;
            if (maxLen != null) {
                len = maxLen.intValue();
            }
            if (len > 0) {
                value = value.substring(0, Math.min(len, value.length()));
            }
        }
        if (display == null) {
            display = value;
        }
        if (PdfName.TX.equals(type) || PdfName.CH.equals(type)) {
            PdfString v = new PdfString(value, PdfObject.TEXT_UNICODE);
            for (int idx = 0; idx < item.size(); idx++) {
                PdfDictionary valueDic = item.getValue(idx);
                valueDic.put(PdfName.V, v);
                valueDic.remove(PdfName.I);
                markUsed(valueDic);
                PdfDictionary merged2 = item.getMerged(idx);
                merged2.remove(PdfName.I);
                merged2.put(PdfName.V, v);
                PdfDictionary widget = item.getWidget(idx);
                if (this.generateAppearances) {
                    PdfAppearance app = getAppearance(merged2, display, name);
                    if (PdfName.CH.equals(type)) {
                        PdfNumber n = new PdfNumber(this.topFirst);
                        widget.put(PdfName.TI, n);
                        merged2.put(PdfName.TI, n);
                    }
                    PdfDictionary appDic = widget.getAsDict(PdfName.AP);
                    if (appDic == null) {
                        appDic = new PdfDictionary();
                        widget.put(PdfName.AP, appDic);
                        merged2.put(PdfName.AP, appDic);
                    }
                    appDic.put(PdfName.N, app.getIndirectReference());
                    this.writer.releaseTemplate(app);
                } else {
                    widget.remove(PdfName.AP);
                    merged2.remove(PdfName.AP);
                }
                markUsed(widget);
            }
            return true;
        }
        if (PdfName.BTN.equals(type)) {
            PdfNumber ff = item.getMerged(0).getAsNumber(PdfName.FF);
            int flags = 0;
            if (ff != null) {
                flags = ff.intValue();
            }
            if ((flags & 65536) != 0) {
                try {
                    Image img = Image.getInstance(Base64.decode(value));
                    PushbuttonField pb = getNewPushbuttonFromField(name);
                    pb.setImage(img);
                    replacePushbuttonField(name, pb.getField());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            PdfName v2 = new PdfName(value);
            ArrayList lopt = new ArrayList();
            PdfArray opts = item.getValue(0).getAsArray(PdfName.OPT);
            if (opts != null) {
                for (int k = 0; k < opts.size(); k++) {
                    PdfString valStr = opts.getAsString(k);
                    if (valStr != null) {
                        lopt.add(valStr.toUnicodeString());
                    } else {
                        lopt.add(null);
                    }
                }
            }
            int vidx = lopt.indexOf(value);
            PdfName valt = null;
            if (vidx >= 0) {
                PdfName pdfName = new PdfName(String.valueOf(vidx));
                valt = pdfName;
                vt = pdfName;
            } else {
                vt = v2;
            }
            for (int idx2 = 0; idx2 < item.size(); idx2++) {
                PdfDictionary merged3 = item.getMerged(idx2);
                PdfDictionary widget2 = item.getWidget(idx2);
                PdfDictionary valDict = item.getValue(idx2);
                markUsed(item.getValue(idx2));
                if (valt != null) {
                    PdfString ps = new PdfString(value, PdfObject.TEXT_UNICODE);
                    valDict.put(PdfName.V, ps);
                    merged3.put(PdfName.V, ps);
                } else {
                    valDict.put(PdfName.V, v2);
                    merged3.put(PdfName.V, v2);
                }
                markUsed(widget2);
                if (isInAP(widget2, vt)) {
                    merged3.put(PdfName.AS, vt);
                    widget2.put(PdfName.AS, vt);
                } else {
                    merged3.put(PdfName.AS, PdfName.Off);
                    widget2.put(PdfName.AS, PdfName.Off);
                }
            }
            return true;
        }
        return false;
    }

    public boolean setListSelection(String name, String[] value) throws IOException, DocumentException {
        Item item = getFieldItem(name);
        if (item == null) {
            return false;
        }
        PdfName type = item.getMerged(0).getAsName(PdfName.FT);
        if (!PdfName.CH.equals(type)) {
            return false;
        }
        String[] options = getListOptionExport(name);
        PdfArray array = new PdfArray();
        for (String element : value) {
            for (int j = 0; j < options.length; j++) {
                if (options[j].equals(element)) {
                    array.add(new PdfNumber(j));
                }
            }
        }
        item.writeToAll(PdfName.I, array, 5);
        item.writeToAll(PdfName.V, null, 5);
        item.writeToAll(PdfName.AP, null, 3);
        item.markUsed(this, 6);
        return true;
    }

    boolean isInAP(PdfDictionary dic, PdfName check) {
        PdfDictionary NDic;
        PdfDictionary appDic = dic.getAsDict(PdfName.AP);
        return (appDic == null || (NDic = appDic.getAsDict(PdfName.N)) == null || NDic.get(check) == null) ? false : true;
    }

    public HashMap<String, Item> getFields() {
        return this.fields;
    }

    public Item getFieldItem(String name) {
        if (this.xfa.isXfaPresent()) {
            name = this.xfa.findFieldName(name, this);
            if (name == null) {
                return null;
            }
        }
        return this.fields.get(name);
    }

    public String getTranslatedFieldName(String name) {
        String namex;
        if (this.xfa.isXfaPresent() && (namex = this.xfa.findFieldName(name, this)) != null) {
            name = namex;
        }
        return name;
    }

    public float[] getFieldPositions(String name) {
        Item item = getFieldItem(name);
        if (item == null) {
            return null;
        }
        float[] ret = new float[item.size() * 5];
        int ptr = 0;
        for (int k = 0; k < item.size(); k++) {
            try {
                PdfDictionary wd = item.getWidget(k);
                PdfArray rect = wd.getAsArray(PdfName.RECT);
                if (rect != null) {
                    Rectangle r = PdfReader.getNormalizedRectangle(rect);
                    int page = item.getPage(k).intValue();
                    int rotation = this.reader.getPageRotation(page);
                    int i = ptr;
                    int ptr2 = ptr + 1;
                    ret[i] = page;
                    if (rotation != 0) {
                        Rectangle pageSize = this.reader.getPageSize(page);
                        switch (rotation) {
                            case 90:
                                r = new Rectangle(r.getBottom(), pageSize.getRight() - r.getLeft(), r.getTop(), pageSize.getRight() - r.getRight());
                                break;
                            case 180:
                                r = new Rectangle(pageSize.getRight() - r.getLeft(), pageSize.getTop() - r.getBottom(), pageSize.getRight() - r.getRight(), pageSize.getTop() - r.getTop());
                                break;
                            case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                                r = new Rectangle(pageSize.getTop() - r.getBottom(), r.getLeft(), pageSize.getTop() - r.getTop(), r.getRight());
                                break;
                        }
                        r.normalize();
                    }
                    int ptr3 = ptr2 + 1;
                    ret[ptr2] = r.getLeft();
                    int ptr4 = ptr3 + 1;
                    ret[ptr3] = r.getBottom();
                    int ptr5 = ptr4 + 1;
                    ret[ptr4] = r.getRight();
                    ptr = ptr5 + 1;
                    ret[ptr5] = r.getTop();
                }
            } catch (Exception e) {
            }
        }
        if (ptr < ret.length) {
            float[] ret2 = new float[ptr];
            System.arraycopy(ret, 0, ret2, 0, ptr);
            return ret2;
        }
        return ret;
    }

    private int removeRefFromArray(PdfArray array, PdfObject refo) {
        if (refo == null || !refo.isIndirect()) {
            return array.size();
        }
        PdfIndirectReference ref = (PdfIndirectReference) refo;
        int j = 0;
        while (j < array.size()) {
            PdfObject obj = array.getPdfObject(j);
            if (obj.isIndirect() && ((PdfIndirectReference) obj).getNumber() == ref.getNumber()) {
                int i = j;
                j--;
                array.remove(i);
            }
            j++;
        }
        return array.size();
    }

    public boolean removeFieldsFromPage(int page) {
        if (page < 1) {
            return false;
        }
        String[] names = new String[this.fields.size()];
        this.fields.keySet().toArray(names);
        boolean found = false;
        for (String name : names) {
            boolean fr = removeField(name, page);
            found = found || fr;
        }
        return found;
    }

    public boolean removeField(String name, int page) {
        PdfDictionary acroForm;
        PdfArray arrayf;
        PdfIndirectReference ref;
        Item item = getFieldItem(name);
        if (item == null || (acroForm = (PdfDictionary) PdfReader.getPdfObject(this.reader.getCatalog().get(PdfName.ACROFORM), this.reader.getCatalog())) == null || (arrayf = acroForm.getAsArray(PdfName.FIELDS)) == null) {
            return false;
        }
        int k = 0;
        while (k < item.size()) {
            int pageV = item.getPage(k).intValue();
            if (page == -1 || page == pageV) {
                PdfIndirectReference ref2 = item.getWidgetRef(k);
                PdfDictionary wd = item.getWidget(k);
                PdfDictionary pageDic = this.reader.getPageN(pageV);
                PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
                if (annots != null) {
                    if (removeRefFromArray(annots, ref2) == 0) {
                        pageDic.remove(PdfName.ANNOTS);
                        markUsed(pageDic);
                    } else {
                        markUsed(annots);
                    }
                }
                PdfReader.killIndirect(ref2);
                PdfIndirectReference kid = ref2;
                while (true) {
                    ref = wd.getAsIndirectObject(PdfName.PARENT);
                    if (ref == null) {
                        break;
                    }
                    wd = wd.getAsDict(PdfName.PARENT);
                    PdfArray kids = wd.getAsArray(PdfName.KIDS);
                    if (removeRefFromArray(kids, kid) != 0) {
                        break;
                    }
                    kid = ref;
                    PdfReader.killIndirect(ref);
                }
                if (ref == null) {
                    removeRefFromArray(arrayf, kid);
                    markUsed(arrayf);
                }
                if (page != -1) {
                    item.remove(k);
                    k--;
                }
            }
            k++;
        }
        if (page == -1 || item.size() == 0) {
            this.fields.remove(name);
            return true;
        }
        return true;
    }

    public boolean removeField(String name) {
        return removeField(name, -1);
    }

    public boolean isGenerateAppearances() {
        return this.generateAppearances;
    }

    public void setGenerateAppearances(boolean generateAppearances) {
        this.generateAppearances = generateAppearances;
        PdfDictionary top = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
        if (generateAppearances) {
            top.remove(PdfName.NEEDAPPEARANCES);
        } else {
            top.put(PdfName.NEEDAPPEARANCES, PdfBoolean.PDFTRUE);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/AcroFields$Item.class */
    public static class Item {
        public static final int WRITE_MERGED = 1;
        public static final int WRITE_WIDGET = 2;
        public static final int WRITE_VALUE = 4;

        @Deprecated
        public ArrayList values = new ArrayList();

        @Deprecated
        public ArrayList widgets = new ArrayList();

        @Deprecated
        public ArrayList widget_refs = new ArrayList();

        @Deprecated
        public ArrayList merged = new ArrayList();

        @Deprecated
        public ArrayList page = new ArrayList();

        @Deprecated
        public ArrayList tabOrder = new ArrayList();

        public void writeToAll(PdfName key, PdfObject value, int writeFlags) {
            if ((writeFlags & 1) != 0) {
                for (int i = 0; i < this.merged.size(); i++) {
                    PdfDictionary curDict = getMerged(i);
                    curDict.put(key, value);
                }
            }
            if ((writeFlags & 2) != 0) {
                for (int i2 = 0; i2 < this.widgets.size(); i2++) {
                    PdfDictionary curDict2 = getWidget(i2);
                    curDict2.put(key, value);
                }
            }
            if ((writeFlags & 4) != 0) {
                for (int i3 = 0; i3 < this.values.size(); i3++) {
                    PdfDictionary curDict3 = getValue(i3);
                    curDict3.put(key, value);
                }
            }
        }

        public void markUsed(AcroFields parentFields, int writeFlags) {
            if ((writeFlags & 4) != 0) {
                for (int i = 0; i < size(); i++) {
                    parentFields.markUsed(getValue(i));
                }
            }
            if ((writeFlags & 2) != 0) {
                for (int i2 = 0; i2 < size(); i2++) {
                    parentFields.markUsed(getWidget(i2));
                }
            }
        }

        public int size() {
            return this.values.size();
        }

        void remove(int killIdx) {
            this.values.remove(killIdx);
            this.widgets.remove(killIdx);
            this.widget_refs.remove(killIdx);
            this.merged.remove(killIdx);
            this.page.remove(killIdx);
            this.tabOrder.remove(killIdx);
        }

        public PdfDictionary getValue(int idx) {
            return (PdfDictionary) this.values.get(idx);
        }

        void addValue(PdfDictionary value) {
            this.values.add(value);
        }

        public PdfDictionary getWidget(int idx) {
            return (PdfDictionary) this.widgets.get(idx);
        }

        void addWidget(PdfDictionary widget) {
            this.widgets.add(widget);
        }

        public PdfIndirectReference getWidgetRef(int idx) {
            return (PdfIndirectReference) this.widget_refs.get(idx);
        }

        void addWidgetRef(PdfIndirectReference widgRef) {
            this.widget_refs.add(widgRef);
        }

        public PdfDictionary getMerged(int idx) {
            return (PdfDictionary) this.merged.get(idx);
        }

        void addMerged(PdfDictionary mergeDict) {
            this.merged.add(mergeDict);
        }

        public Integer getPage(int idx) {
            return (Integer) this.page.get(idx);
        }

        void addPage(int pg) {
            this.page.add(new Integer(pg));
        }

        void forcePage(int idx, int pg) {
            this.page.set(idx, new Integer(pg));
        }

        public Integer getTabOrder(int idx) {
            return (Integer) this.tabOrder.get(idx);
        }

        void addTabOrder(int order) {
            this.tabOrder.add(new Integer(order));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/AcroFields$InstHit.class */
    private static class InstHit {
        IntHashtable hits;

        public InstHit(int[] inst) {
            if (inst == null) {
                return;
            }
            this.hits = new IntHashtable();
            for (int element : inst) {
                this.hits.put(element, 1);
            }
        }

        public boolean isHit(int n) {
            if (this.hits == null) {
                return true;
            }
            return this.hits.containsKey(n);
        }
    }

    public List<String> getSignatureNames() {
        PdfDictionary v;
        PdfArray ro;
        int rangeSize;
        if (this.sigNames != null) {
            return new ArrayList(this.sigNames.keySet());
        }
        this.sigNames = new HashMap();
        ArrayList sorter = new ArrayList();
        for (Object element : this.fields.entrySet()) {
            Map.Entry<String, Item> entry = (Map.Entry) element;
            Item item = entry.getValue();
            PdfDictionary merged = item.getMerged(0);
            if (PdfName.SIG.equals(merged.get(PdfName.FT)) && (v = merged.getAsDict(PdfName.V)) != null) {
                PdfString contents = v.getAsString(PdfName.CONTENTS);
                if (contents != null && (ro = v.getAsArray(PdfName.BYTERANGE)) != null && (rangeSize = ro.size()) >= 2) {
                    int length = ro.getAsNumber(rangeSize - 1).intValue() + ro.getAsNumber(rangeSize - 2).intValue();
                    sorter.add(new Object[]{entry.getKey(), new int[]{length, 0}});
                }
            }
        }
        Collections.sort(sorter, new SorterComparator());
        if (!sorter.isEmpty()) {
            if (((int[]) ((Object[]) sorter.get(sorter.size() - 1))[1])[0] == this.reader.getFileLength()) {
                this.totalRevisions = sorter.size();
            } else {
                this.totalRevisions = sorter.size() + 1;
            }
            for (int k = 0; k < sorter.size(); k++) {
                Object[] objs = (Object[]) sorter.get(k);
                String name = (String) objs[0];
                int[] p = (int[]) objs[1];
                p[1] = k + 1;
                this.sigNames.put(name, p);
            }
        }
        return new ArrayList(this.sigNames.keySet());
    }

    public List<String> getBlankSignatureNames() {
        getSignatureNames();
        ArrayList sigs = new ArrayList();
        for (Object element : this.fields.entrySet()) {
            Map.Entry<String, Item> entry = (Map.Entry) element;
            Item item = entry.getValue();
            PdfDictionary merged = item.getMerged(0);
            if (PdfName.SIG.equals(merged.getAsName(PdfName.FT)) && !this.sigNames.containsKey(entry.getKey())) {
                sigs.add(entry.getKey());
            }
        }
        return sigs;
    }

    public PdfDictionary getSignatureDictionary(String name) {
        getSignatureNames();
        String name2 = getTranslatedFieldName(name);
        if (!this.sigNames.containsKey(name2)) {
            return null;
        }
        Item item = this.fields.get(name2);
        PdfDictionary merged = item.getMerged(0);
        return merged.getAsDict(PdfName.V);
    }

    public boolean signatureCoversWholeDocument(String name) {
        getSignatureNames();
        String name2 = getTranslatedFieldName(name);
        return this.sigNames.containsKey(name2) && ((int[]) this.sigNames.get(name2))[0] == this.reader.getFileLength();
    }

    public PdfPKCS7 verifySignature(String name) {
        return verifySignature(name, null);
    }

    public PdfPKCS7 verifySignature(String name, String provider) {
        PdfPKCS7 pk;
        PdfDictionary v = getSignatureDictionary(name);
        if (v == null) {
            return null;
        }
        try {
            PdfName sub = v.getAsName(PdfName.SUBFILTER);
            PdfString contents = v.getAsString(PdfName.CONTENTS);
            if (sub.equals(PdfName.ADBE_X509_RSA_SHA1)) {
                PdfString cert = v.getAsString(PdfName.CERT);
                pk = new PdfPKCS7(contents.getOriginalBytes(), cert.getBytes(), provider);
            } else {
                pk = new PdfPKCS7(contents.getOriginalBytes(), provider);
            }
            updateByteRange(pk, v);
            PdfString str = v.getAsString(PdfName.M);
            if (str != null) {
                pk.setSignDate(PdfDate.decode(str.toString()));
            }
            PdfObject obj = PdfReader.getPdfObject(v.get(PdfName.NAME));
            if (obj != null) {
                if (obj.isString()) {
                    pk.setSignName(((PdfString) obj).toUnicodeString());
                } else if (obj.isName()) {
                    pk.setSignName(PdfName.decodeName(obj.toString()));
                }
            }
            PdfString str2 = v.getAsString(PdfName.REASON);
            if (str2 != null) {
                pk.setReason(str2.toUnicodeString());
            }
            PdfString str3 = v.getAsString(PdfName.LOCATION);
            if (str3 != null) {
                pk.setLocation(str3.toUnicodeString());
            }
            PdfString str4 = v.getAsString(PdfName.CONTACTINFO);
            if (str4 != null) {
                pk.setContactInfo(str4.toUnicodeString());
            }
            return pk;
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x009a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateByteRange(com.aowagie.text.pdf.PdfPKCS7 r7, com.aowagie.text.pdf.PdfDictionary r8) {
        /*
            r6 = this;
            r0 = r8
            com.aowagie.text.pdf.PdfName r1 = com.aowagie.text.pdf.PdfName.BYTERANGE
            com.aowagie.text.pdf.PdfArray r0 = r0.getAsArray(r1)
            r9 = r0
            r0 = r6
            com.aowagie.text.pdf.PdfReader r0 = r0.reader
            com.aowagie.text.pdf.RandomAccessFileOrArray r0 = r0.getSafeFile()
            r10 = r0
            r0 = r10
            r0.reOpen()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> L9a
            r0 = 8192(0x2000, float:1.148E-41)
            byte[] r0 = new byte[r0]     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> L9a
            r11 = r0
            r0 = 0
            r12 = r0
        L20:
            r0 = r12
            r1 = r9
            int r1 = r1.size()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> L9a
            if (r0 >= r1) goto L81
            r0 = r9
            r1 = r12
            com.aowagie.text.pdf.PdfNumber r0 = r0.getAsNumber(r1)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> L9a
            int r0 = r0.intValue()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> L9a
            r13 = r0
            r0 = r9
            int r12 = r12 + 1
            r1 = r12
            com.aowagie.text.pdf.PdfNumber r0 = r0.getAsNumber(r1)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> L9a
            int r0 = r0.intValue()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> L9a
            r14 = r0
            r0 = r10
            r1 = r13
            r0.seek(r1)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> L9a
        L49:
            r0 = r14
            if (r0 <= 0) goto L7b
            r0 = r10
            r1 = r11
            r2 = 0
            r3 = r14
            r4 = r11
            int r4 = r4.length     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> L9a
            int r3 = java.lang.Math.min(r3, r4)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> L9a
            int r0 = r0.read(r1, r2, r3)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> L9a
            r15 = r0
            r0 = r15
            if (r0 > 0) goto L68
            goto L7b
        L68:
            r0 = r14
            r1 = r15
            int r0 = r0 - r1
            r14 = r0
            r0 = r7
            r1 = r11
            r2 = 0
            r3 = r15
            r0.update(r1, r2, r3)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> L9a
            goto L49
        L7b:
            int r12 = r12 + 1
            goto L20
        L81:
            r0 = r10
            r0.close()     // Catch: java.lang.Exception -> L89
            goto La9
        L89:
            r11 = move-exception
            goto La9
        L8e:
            r11 = move-exception
            com.aowagie.text.ExceptionConverter r0 = new com.aowagie.text.ExceptionConverter     // Catch: java.lang.Throwable -> L9a
            r1 = r0
            r2 = r11
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L9a
            throw r0     // Catch: java.lang.Throwable -> L9a
        L9a:
            r16 = move-exception
            r0 = r10
            r0.close()     // Catch: java.lang.Exception -> La4
            goto La6
        La4:
            r17 = move-exception
        La6:
            r0 = r16
            throw r0
        La9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.aowagie.text.pdf.AcroFields.updateByteRange(com.aowagie.text.pdf.PdfPKCS7, com.aowagie.text.pdf.PdfDictionary):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void markUsed(PdfObject obj) {
        if (!this.append) {
            return;
        }
        ((PdfStamperImp) this.writer).markUsed(obj);
    }

    public int getTotalRevisions() {
        getSignatureNames();
        return this.totalRevisions;
    }

    public int getRevision(String field) {
        getSignatureNames();
        String field2 = getTranslatedFieldName(field);
        if (!this.sigNames.containsKey(field2)) {
            return 0;
        }
        return ((int[]) this.sigNames.get(field2))[1];
    }

    public InputStream extractRevision(String field) throws IOException {
        getSignatureNames();
        String field2 = getTranslatedFieldName(field);
        if (!this.sigNames.containsKey(field2)) {
            return null;
        }
        int length = ((int[]) this.sigNames.get(field2))[0];
        RandomAccessFileOrArray raf = this.reader.getSafeFile();
        raf.reOpen();
        raf.seek(0);
        return new RevisionStream(raf, length);
    }

    public Map getFieldCache() {
        return this.fieldCache;
    }

    public void setFieldCache(Map fieldCache) {
        this.fieldCache = fieldCache;
    }

    public void setExtraMargin(float extraMarginLeft, float extraMarginTop) {
        this.extraMarginLeft = extraMarginLeft;
        this.extraMarginTop = extraMarginTop;
    }

    public void addSubstitutionFont(BaseFont font) {
        if (this.substitutionFonts == null) {
            this.substitutionFonts = new ArrayList();
        }
        this.substitutionFonts.add(font);
    }

    static {
        stdFieldFontNames.put("CoBO", new String[]{"Courier-BoldOblique"});
        stdFieldFontNames.put("CoBo", new String[]{"Courier-Bold"});
        stdFieldFontNames.put("CoOb", new String[]{"Courier-Oblique"});
        stdFieldFontNames.put("Cour", new String[]{"Courier"});
        stdFieldFontNames.put("HeBO", new String[]{"Helvetica-BoldOblique"});
        stdFieldFontNames.put("HeBo", new String[]{"Helvetica-Bold"});
        stdFieldFontNames.put("HeOb", new String[]{"Helvetica-Oblique"});
        stdFieldFontNames.put("Helv", new String[]{"Helvetica"});
        stdFieldFontNames.put("Symb", new String[]{"Symbol"});
        stdFieldFontNames.put("TiBI", new String[]{"Times-BoldItalic"});
        stdFieldFontNames.put("TiBo", new String[]{"Times-Bold"});
        stdFieldFontNames.put("TiIt", new String[]{"Times-Italic"});
        stdFieldFontNames.put("TiRo", new String[]{"Times-Roman"});
        stdFieldFontNames.put("ZaDb", new String[]{"ZapfDingbats"});
        stdFieldFontNames.put("HySm", new String[]{"HYSMyeongJo-Medium", "UniKS-UCS2-H"});
        stdFieldFontNames.put("HyGo", new String[]{"HYGoThic-Medium", "UniKS-UCS2-H"});
        stdFieldFontNames.put("KaGo", new String[]{"HeiseiKakuGo-W5", "UniKS-UCS2-H"});
        stdFieldFontNames.put("KaMi", new String[]{"HeiseiMin-W3", "UniJIS-UCS2-H"});
        stdFieldFontNames.put("MHei", new String[]{"MHei-Medium", "UniCNS-UCS2-H"});
        stdFieldFontNames.put("MSun", new String[]{"MSung-Light", "UniCNS-UCS2-H"});
        stdFieldFontNames.put("STSo", new String[]{"STSong-Light", "UniGB-UCS2-H"});
        buttonRemove = new PdfName[]{PdfName.MK, PdfName.F, PdfName.FF, PdfName.Q, PdfName.BS, PdfName.BORDER};
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/AcroFields$RevisionStream.class */
    private static class RevisionStream extends InputStream {
        private final byte[] b;
        private final RandomAccessFileOrArray raf;
        private final int length;
        private int rangePosition;
        private boolean closed;

        private RevisionStream(RandomAccessFileOrArray raf, int length) {
            this.b = new byte[1];
            this.rangePosition = 0;
            this.raf = raf;
            this.length = length;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int n = read(this.b);
            if (n != 1) {
                return -1;
            }
            return this.b[0] & 255;
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            if (this.rangePosition >= this.length) {
                close();
                return -1;
            }
            int elen = Math.min(len, this.length - this.rangePosition);
            this.raf.readFully(b, off, elen);
            this.rangePosition += elen;
            return elen;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (!this.closed) {
                this.raf.close();
                this.closed = true;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/AcroFields$SorterComparator.class */
    private static class SorterComparator implements Comparator {
        private SorterComparator() {
        }

        @Override // java.util.Comparator
        public int compare(Object o1, Object o2) {
            int n1 = ((int[]) ((Object[]) o1)[1])[0];
            int n2 = ((int[]) ((Object[]) o2)[1])[0];
            return n1 - n2;
        }
    }

    public ArrayList getSubstitutionFonts() {
        return this.substitutionFonts;
    }

    public void setSubstitutionFonts(ArrayList substitutionFonts) {
        this.substitutionFonts = substitutionFonts;
    }

    public XfaForm getXfa() {
        return this.xfa;
    }

    public PushbuttonField getNewPushbuttonFromField(String field) {
        return getNewPushbuttonFromField(field, 0);
    }

    public PushbuttonField getNewPushbuttonFromField(String field, int order) {
        try {
            if (getFieldType(field) != 1) {
                return null;
            }
            Item item = getFieldItem(field);
            if (order >= item.size()) {
                return null;
            }
            int posi = order * 5;
            float[] pos = getFieldPositions(field);
            Rectangle box = new Rectangle(pos[posi + 1], pos[posi + 2], pos[posi + 3], pos[posi + 4]);
            PushbuttonField newButton = new PushbuttonField(this.writer, box, null);
            PdfDictionary dic = item.getMerged(order);
            decodeGenericDictionary(dic, newButton);
            PdfDictionary mk = dic.getAsDict(PdfName.MK);
            if (mk != null) {
                PdfString text = mk.getAsString(PdfName.CA);
                if (text != null) {
                    newButton.setText(text.toUnicodeString());
                }
                PdfNumber tp = mk.getAsNumber(PdfName.TP);
                if (tp != null) {
                    newButton.setLayout(tp.intValue() + 1);
                }
                PdfDictionary ifit = mk.getAsDict(PdfName.IF);
                if (ifit != null) {
                    PdfName sw = ifit.getAsName(PdfName.SW);
                    if (sw != null) {
                        int scale = 1;
                        if (sw.equals(PdfName.B)) {
                            scale = 3;
                        } else if (sw.equals(PdfName.S)) {
                            scale = 4;
                        } else if (sw.equals(PdfName.N)) {
                            scale = 2;
                        }
                        newButton.setScaleIcon(scale);
                    }
                    PdfName sw2 = ifit.getAsName(PdfName.S);
                    if (sw2 != null && sw2.equals(PdfName.A)) {
                        newButton.setProportionalIcon(false);
                    }
                    PdfArray aj = ifit.getAsArray(PdfName.A);
                    if (aj != null && aj.size() == 2) {
                        float left = aj.getAsNumber(0).floatValue();
                        float bottom = aj.getAsNumber(1).floatValue();
                        newButton.setIconHorizontalAdjustment(left);
                        newButton.setIconVerticalAdjustment(bottom);
                    }
                    PdfBoolean fb = ifit.getAsBoolean(PdfName.FB);
                    if (fb != null && fb.booleanValue()) {
                        newButton.setIconFitToBounds(true);
                    }
                }
                PdfObject i = mk.get(PdfName.I);
                if (i != null && i.isIndirect()) {
                    newButton.setIconReference((PRIndirectReference) i);
                }
            }
            return newButton;
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public boolean replacePushbuttonField(String field, PdfFormField button) {
        return replacePushbuttonField(field, button, 0);
    }

    public boolean replacePushbuttonField(String field, PdfFormField button, int order) {
        if (getFieldType(field) != 1) {
            return false;
        }
        Item item = getFieldItem(field);
        if (order >= item.size()) {
            return false;
        }
        PdfDictionary merged = item.getMerged(order);
        PdfDictionary values = item.getValue(order);
        PdfDictionary widgets = item.getWidget(order);
        for (PdfName element : buttonRemove) {
            merged.remove(element);
            values.remove(element);
            widgets.remove(element);
        }
        for (PdfName key : button.getKeys()) {
            if (!key.equals(PdfName.T) && !key.equals(PdfName.RECT)) {
                if (key.equals(PdfName.FF)) {
                    values.put(key, button.get(key));
                } else {
                    widgets.put(key, button.get(key));
                }
                merged.put(key, button.get(key));
            }
        }
        return true;
    }
}
