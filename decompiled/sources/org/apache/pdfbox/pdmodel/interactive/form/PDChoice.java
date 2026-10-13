package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDChoice.class */
public abstract class PDChoice extends PDVariableText {
    static final int FLAG_COMBO = 131072;
    private static final int FLAG_SORT = 524288;
    private static final int FLAG_MULTI_SELECT = 2097152;
    private static final int FLAG_DO_NOT_SPELL_CHECK = 4194304;
    private static final int FLAG_COMMIT_ON_SEL_CHANGE = 67108864;

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField
    abstract void constructAppearances() throws IOException;

    public PDChoice(PDAcroForm acroForm) {
        super(acroForm);
        getCOSObject().setItem(COSName.FT, (COSBase) COSName.CH);
    }

    PDChoice(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public List<String> getOptions() {
        COSBase values = getCOSObject().getDictionaryObject(COSName.OPT);
        return FieldUtils.getPairableItems(values, 0);
    }

    public void setOptions(List<String> displayValues) {
        if (displayValues != null && !displayValues.isEmpty()) {
            if (isSort()) {
                Collections.sort(displayValues);
            }
            getCOSObject().setItem(COSName.OPT, (COSBase) COSArrayList.convertStringListToCOSStringCOSArray(displayValues));
            return;
        }
        getCOSObject().removeItem(COSName.OPT);
    }

    public void setOptions(List<String> exportValues, List<String> displayValues) {
        if (exportValues != null && displayValues != null && !exportValues.isEmpty() && !displayValues.isEmpty()) {
            if (exportValues.size() != displayValues.size()) {
                throw new IllegalArgumentException("The number of entries for exportValue and displayValue shall be the same.");
            }
            List<FieldUtils.KeyValue> keyValuePairs = FieldUtils.toKeyValueList(exportValues, displayValues);
            if (isSort()) {
                FieldUtils.sortByValue(keyValuePairs);
            }
            COSArray options = new COSArray();
            for (int i = 0; i < exportValues.size(); i++) {
                COSArray entry = new COSArray();
                entry.add((COSBase) new COSString(keyValuePairs.get(i).getKey()));
                entry.add((COSBase) new COSString(keyValuePairs.get(i).getValue()));
                options.add((COSBase) entry);
            }
            getCOSObject().setItem(COSName.OPT, (COSBase) options);
            return;
        }
        getCOSObject().removeItem(COSName.OPT);
    }

    public List<String> getOptionsDisplayValues() {
        COSBase values = getCOSObject().getDictionaryObject(COSName.OPT);
        return FieldUtils.getPairableItems(values, 1);
    }

    public List<String> getOptionsExportValues() {
        return getOptions();
    }

    public List<Integer> getSelectedOptionsIndex() {
        COSBase value = getCOSObject().getDictionaryObject(COSName.I);
        if (value != null) {
            return COSArrayList.convertIntegerCOSArrayToList((COSArray) value);
        }
        return Collections.emptyList();
    }

    public void setSelectedOptionsIndex(List<Integer> values) {
        if (values != null && !values.isEmpty()) {
            if (!isMultiSelect()) {
                throw new IllegalArgumentException("Setting the indices is not allowed for choice fields not allowing multiple selections.");
            }
            getCOSObject().setItem(COSName.I, (COSBase) COSArrayList.converterToCOSArray(values));
            return;
        }
        getCOSObject().removeItem(COSName.I);
    }

    public boolean isSort() {
        return getCOSObject().getFlag(COSName.FF, 524288);
    }

    public void setSort(boolean sort) {
        getCOSObject().setFlag(COSName.FF, 524288, sort);
    }

    public boolean isMultiSelect() {
        return getCOSObject().getFlag(COSName.FF, 2097152);
    }

    public void setMultiSelect(boolean multiSelect) {
        getCOSObject().setFlag(COSName.FF, 2097152, multiSelect);
    }

    public boolean isDoNotSpellCheck() {
        return getCOSObject().getFlag(COSName.FF, 4194304);
    }

    public void setDoNotSpellCheck(boolean doNotSpellCheck) {
        getCOSObject().setFlag(COSName.FF, 4194304, doNotSpellCheck);
    }

    public boolean isCommitOnSelChange() {
        return getCOSObject().getFlag(COSName.FF, 67108864);
    }

    public void setCommitOnSelChange(boolean commitOnSelChange) {
        getCOSObject().setFlag(COSName.FF, 67108864, commitOnSelChange);
    }

    public boolean isCombo() {
        return getCOSObject().getFlag(COSName.FF, 131072);
    }

    public void setCombo(boolean combo) {
        getCOSObject().setFlag(COSName.FF, 131072, combo);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public void setValue(String value) throws IOException {
        getCOSObject().setString(COSName.V, value);
        setSelectedOptionsIndex(null);
        applyChange();
    }

    public void setDefaultValue(String value) throws IOException {
        getCOSObject().setString(COSName.DV, value);
    }

    public void setValue(List<String> values) throws IOException {
        if (values != null && !values.isEmpty()) {
            if (!isMultiSelect()) {
                throw new IllegalArgumentException("The list box does not allow multiple selections.");
            }
            if (!getOptions().containsAll(values)) {
                throw new IllegalArgumentException("The values are not contained in the selectable options.");
            }
            getCOSObject().setItem(COSName.V, (COSBase) COSArrayList.convertStringListToCOSStringCOSArray(values));
            updateSelectedOptionsIndex(values);
        } else {
            getCOSObject().removeItem(COSName.V);
            getCOSObject().removeItem(COSName.I);
        }
        applyChange();
    }

    public List<String> getValue() {
        return getValueFor(COSName.V);
    }

    public List<String> getDefaultValue() {
        return getValueFor(COSName.DV);
    }

    private List<String> getValueFor(COSName name) {
        COSBase value = getCOSObject().getDictionaryObject(name);
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

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public String getValueAsString() {
        return Arrays.toString(getValue().toArray());
    }

    private void updateSelectedOptionsIndex(List<String> values) {
        List<String> options = getOptions();
        List<Integer> indices = new ArrayList<>();
        for (String value : values) {
            indices.add(Integer.valueOf(options.indexOf(value)));
        }
        Collections.sort(indices);
        setSelectedOptionsIndex(indices);
    }
}
