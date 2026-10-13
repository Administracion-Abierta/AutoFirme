package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceEntry;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDButton.class */
public abstract class PDButton extends PDTerminalField {
    static final int FLAG_RADIO = 32768;
    static final int FLAG_PUSHBUTTON = 65536;
    static final int FLAG_RADIOS_IN_UNISON = 33554432;

    public PDButton(PDAcroForm acroForm) {
        super(acroForm);
        getCOSObject().setItem(COSName.FT, (COSBase) COSName.BTN);
    }

    PDButton(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public boolean isPushButton() {
        return getCOSObject().getFlag(COSName.FF, 65536);
    }

    @Deprecated
    public void setPushButton(boolean pushbutton) {
        getCOSObject().setFlag(COSName.FF, 65536, pushbutton);
        if (pushbutton) {
            setRadioButton(false);
        }
    }

    public boolean isRadioButton() {
        return getCOSObject().getFlag(COSName.FF, 32768);
    }

    @Deprecated
    public void setRadioButton(boolean radiobutton) {
        getCOSObject().setFlag(COSName.FF, 32768, radiobutton);
        if (radiobutton) {
            setPushButton(false);
        }
    }

    public String getValue() {
        COSBase value = getInheritableAttribute(COSName.V);
        if (value instanceof COSName) {
            String stringValue = ((COSName) value).getName();
            List<String> exportValues = getExportValues();
            if (!exportValues.isEmpty()) {
                try {
                    int idx = Integer.parseInt(stringValue, 10);
                    if (idx >= 0 && idx < exportValues.size()) {
                        return exportValues.get(idx);
                    }
                } catch (NumberFormatException e) {
                    return stringValue;
                }
            }
            return stringValue;
        }
        return "Off";
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public void setValue(String value) throws IOException {
        checkValue(value);
        boolean hasExportValues = getExportValues().size() > 0;
        if (hasExportValues) {
            updateByOption(value);
        } else {
            updateByValue(value);
        }
        applyChange();
    }

    public void setValue(int index) throws IOException {
        if (getExportValues().isEmpty() || index < 0 || index >= getExportValues().size()) {
            throw new IllegalArgumentException("index '" + index + "' is not a valid index for the field " + getFullyQualifiedName() + ", valid indices are from 0 to " + (getExportValues().size() - 1));
        }
        updateByValue(String.valueOf(index));
        applyChange();
    }

    public String getDefaultValue() {
        COSBase value = getInheritableAttribute(COSName.DV);
        if (value instanceof COSName) {
            return ((COSName) value).getName();
        }
        return "";
    }

    public void setDefaultValue(String value) {
        checkValue(value);
        getCOSObject().setName(COSName.DV, value);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public String getValueAsString() {
        return getValue();
    }

    public List<String> getExportValues() {
        COSBase value = getInheritableAttribute(COSName.OPT);
        if (value instanceof COSString) {
            List<String> array = new ArrayList<>();
            array.add(((COSString) value).getString());
            return array;
        }
        if (value instanceof COSArray) {
            return COSArrayList.convertCOSStringCOSArrayToList((COSArray) value);
        }
        return Collections.emptyList();
    }

    public void setExportValues(List<String> values) {
        if (values != null && !values.isEmpty()) {
            COSArray cosValues = COSArrayList.convertStringListToCOSStringCOSArray(values);
            getCOSObject().setItem(COSName.OPT, (COSBase) cosValues);
        } else {
            getCOSObject().removeItem(COSName.OPT);
        }
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField
    void constructAppearances() throws IOException {
        List<String> exportValues = getExportValues();
        if (exportValues.size() > 0) {
            try {
                int optionsIndex = Integer.parseInt(getValue());
                if (optionsIndex < exportValues.size()) {
                    updateByOption(exportValues.get(optionsIndex));
                }
                return;
            } catch (NumberFormatException e) {
                return;
            }
        }
        updateByValue(getValue());
    }

    public Set<String> getOnValues() {
        Set<String> onValues = new LinkedHashSet<>();
        if (getExportValues().size() > 0) {
            onValues.addAll(getExportValues());
            return onValues;
        }
        List<PDAnnotationWidget> widgets = getWidgets();
        for (PDAnnotationWidget widget : widgets) {
            onValues.add(getOnValueForWidget(widget));
        }
        return onValues;
    }

    private String getOnValue(int index) {
        List<PDAnnotationWidget> widgets = getWidgets();
        if (index < widgets.size()) {
            return getOnValueForWidget(widgets.get(index));
        }
        return "";
    }

    private String getOnValueForWidget(PDAnnotationWidget widget) {
        PDAppearanceEntry normalAppearance;
        PDAppearanceDictionary apDictionary = widget.getAppearance();
        if (apDictionary != null && (normalAppearance = apDictionary.getNormalAppearance()) != null) {
            Set<COSName> entries = normalAppearance.getSubDictionary().keySet();
            for (COSName entry : entries) {
                if (COSName.Off.compareTo(entry) != 0) {
                    return entry.getName();
                }
            }
            return "";
        }
        return "";
    }

    void checkValue(String value) {
        Set<String> onValues = getOnValues();
        if (COSName.Off.getName().compareTo(value) != 0 && !onValues.contains(value)) {
            throw new IllegalArgumentException("value '" + value + "' is not a valid option for the field " + getFullyQualifiedName() + ", valid values are: " + onValues + " and " + COSName.Off.getName());
        }
    }

    private void updateByValue(String value) throws IOException {
        getCOSObject().setName(COSName.V, value);
        for (PDAnnotationWidget widget : getWidgets()) {
            if (widget.getAppearance() != null) {
                PDAppearanceEntry appearanceEntry = widget.getAppearance().getNormalAppearance();
                if (((COSDictionary) appearanceEntry.getCOSObject()).containsKey(value)) {
                    widget.setAppearanceState(value);
                } else {
                    widget.setAppearanceState(COSName.Off.getName());
                }
            }
        }
    }

    private void updateByOption(String value) throws IOException {
        List<PDAnnotationWidget> widgets = getWidgets();
        List<String> options = getExportValues();
        if (widgets.size() != options.size()) {
            throw new IllegalArgumentException("The number of options doesn't match the number of widgets");
        }
        if (value.equals(COSName.Off.getName())) {
            updateByValue(value);
            return;
        }
        int optionsIndex = options.indexOf(value);
        if (optionsIndex != -1) {
            updateByValue(getOnValue(optionsIndex));
        }
    }
}
