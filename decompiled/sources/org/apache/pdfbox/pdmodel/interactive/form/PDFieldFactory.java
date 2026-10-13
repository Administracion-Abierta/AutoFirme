package org.apache.pdfbox.pdmodel.interactive.form;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDFieldFactory.class */
public final class PDFieldFactory {
    private static final String FIELD_TYPE_TEXT = "Tx";
    private static final String FIELD_TYPE_BUTTON = "Btn";
    private static final String FIELD_TYPE_CHOICE = "Ch";
    private static final String FIELD_TYPE_SIGNATURE = "Sig";

    private PDFieldFactory() {
    }

    public static PDField createField(PDAcroForm form, COSDictionary field, PDNonTerminalField parent) {
        COSArray kids;
        String fieldType = findFieldType(field);
        if (field.containsKey(COSName.KIDS) && (kids = (COSArray) field.getDictionaryObject(COSName.KIDS)) != null && kids.size() > 0) {
            for (int i = 0; i < kids.size(); i++) {
                COSBase kid = kids.getObject(i);
                if ((kid instanceof COSDictionary) && ((COSDictionary) kid).getString(COSName.T) != null) {
                    return new PDNonTerminalField(form, field, parent);
                }
            }
        }
        if (FIELD_TYPE_CHOICE.equals(fieldType)) {
            return createChoiceSubType(form, field, parent);
        }
        if (FIELD_TYPE_TEXT.equals(fieldType)) {
            return new PDTextField(form, field, parent);
        }
        if (FIELD_TYPE_SIGNATURE.equals(fieldType)) {
            return new PDSignatureField(form, field, parent);
        }
        if (FIELD_TYPE_BUTTON.equals(fieldType)) {
            return createButtonSubType(form, field, parent);
        }
        return null;
    }

    private static PDField createChoiceSubType(PDAcroForm form, COSDictionary field, PDNonTerminalField parent) {
        int flags = field.getInt(COSName.FF, 0);
        if ((flags & 131072) != 0) {
            return new PDComboBox(form, field, parent);
        }
        return new PDListBox(form, field, parent);
    }

    private static PDField createButtonSubType(PDAcroForm form, COSDictionary field, PDNonTerminalField parent) {
        int flags = field.getInt(COSName.FF, 0);
        if ((flags & 32768) != 0) {
            return new PDRadioButton(form, field, parent);
        }
        if ((flags & 65536) != 0) {
            return new PDPushButton(form, field, parent);
        }
        return new PDCheckBox(form, field, parent);
    }

    private static String findFieldType(COSDictionary dic) {
        String retval = dic.getNameAsString(COSName.FT);
        if (retval == null) {
            COSBase base = dic.getDictionaryObject(COSName.PARENT, COSName.P);
            if (base instanceof COSDictionary) {
                retval = findFieldType((COSDictionary) base);
            }
        }
        return retval;
    }
}
