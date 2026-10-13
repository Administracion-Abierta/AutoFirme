package org.apache.pdfbox.pdmodel.graphics.optionalcontent;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/optionalcontent/PDOptionalContentProperties.class */
public class PDOptionalContentProperties implements COSObjectable {
    private final COSDictionary dict;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/optionalcontent/PDOptionalContentProperties$BaseState.class */
    public enum BaseState {
        ON(COSName.ON),
        OFF(COSName.OFF),
        UNCHANGED(COSName.UNCHANGED);

        private final COSName name;

        BaseState(COSName value) {
            this.name = value;
        }

        public COSName getName() {
            return this.name;
        }

        public static BaseState valueOf(COSName state) {
            if (state == null) {
                return ON;
            }
            return valueOf(state.getName().toUpperCase());
        }
    }

    public PDOptionalContentProperties() {
        this.dict = new COSDictionary();
        this.dict.setItem(COSName.OCGS, (COSBase) new COSArray());
        COSDictionary d = new COSDictionary();
        d.setString(COSName.NAME, "Top");
        this.dict.setItem(COSName.D, (COSBase) d);
    }

    public PDOptionalContentProperties(COSDictionary props) {
        this.dict = props;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dict;
    }

    private COSArray getOCGs() {
        COSArray ocgs = this.dict.getCOSArray(COSName.OCGS);
        if (ocgs == null) {
            ocgs = new COSArray();
            this.dict.setItem(COSName.OCGS, (COSBase) ocgs);
        }
        return ocgs;
    }

    private COSDictionary getD() {
        COSBase base = this.dict.getDictionaryObject(COSName.D);
        if (base instanceof COSDictionary) {
            return (COSDictionary) base;
        }
        COSDictionary d = new COSDictionary();
        d.setString(COSName.NAME, "Top");
        this.dict.setItem(COSName.D, (COSBase) d);
        return d;
    }

    public PDOptionalContentGroup getGroup(String name) {
        COSArray ocgs = getOCGs();
        for (COSBase o : ocgs) {
            COSDictionary ocg = toDictionary(o);
            String groupName = ocg.getString(COSName.NAME);
            if (groupName.equals(name)) {
                return new PDOptionalContentGroup(ocg);
            }
        }
        return null;
    }

    public void addGroup(PDOptionalContentGroup ocg) {
        COSArray ocgs = getOCGs();
        ocgs.add((COSBase) ocg.getCOSObject());
        COSArray order = (COSArray) getD().getDictionaryObject(COSName.ORDER);
        if (order == null) {
            order = new COSArray();
            getD().setItem(COSName.ORDER, (COSBase) order);
        }
        order.add(ocg);
    }

    public Collection<PDOptionalContentGroup> getOptionalContentGroups() {
        Collection<PDOptionalContentGroup> coll = new ArrayList<>();
        COSArray ocgs = getOCGs();
        for (COSBase base : ocgs) {
            coll.add(new PDOptionalContentGroup(toDictionary(base)));
        }
        return coll;
    }

    public BaseState getBaseState() {
        COSDictionary d = getD();
        COSName name = (COSName) d.getItem(COSName.BASE_STATE);
        return BaseState.valueOf(name);
    }

    public void setBaseState(BaseState state) {
        COSDictionary d = getD();
        d.setItem(COSName.BASE_STATE, (COSBase) state.getName());
    }

    public String[] getGroupNames() {
        COSArray ocgs = this.dict.getCOSArray(COSName.OCGS);
        if (ocgs == null) {
            return new String[0];
        }
        int size = ocgs.size();
        String[] groups = new String[size];
        for (int i = 0; i < size; i++) {
            COSBase obj = ocgs.get(i);
            COSDictionary ocg = toDictionary(obj);
            groups[i] = ocg.getString(COSName.NAME);
        }
        return groups;
    }

    public boolean hasGroup(String groupName) {
        String[] layers = getGroupNames();
        for (String layer : layers) {
            if (layer.equals(groupName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isGroupEnabled(String groupName) {
        boolean result = false;
        COSArray ocgs = getOCGs();
        for (COSBase o : ocgs) {
            COSDictionary ocg = toDictionary(o);
            String name = ocg.getString(COSName.NAME);
            if (groupName.equals(name) && isGroupEnabled(new PDOptionalContentGroup(ocg))) {
                result = true;
            }
        }
        return result;
    }

    public boolean isGroupEnabled(PDOptionalContentGroup group) {
        BaseState baseState = getBaseState();
        boolean enabled = !baseState.equals(BaseState.OFF);
        if (group == null) {
            return enabled;
        }
        COSDictionary d = getD();
        COSBase base = d.getDictionaryObject(COSName.ON);
        if (base instanceof COSArray) {
            for (COSBase o : (COSArray) base) {
                COSDictionary dictionary = toDictionary(o);
                if (dictionary == group.getCOSObject()) {
                    return true;
                }
            }
        }
        COSBase base2 = d.getDictionaryObject(COSName.OFF);
        if (base2 instanceof COSArray) {
            for (COSBase o2 : (COSArray) base2) {
                COSDictionary dictionary2 = toDictionary(o2);
                if (dictionary2 == group.getCOSObject()) {
                    return false;
                }
            }
        }
        return enabled;
    }

    private COSDictionary toDictionary(COSBase o) {
        if (o instanceof COSObject) {
            return (COSDictionary) ((COSObject) o).getObject();
        }
        return (COSDictionary) o;
    }

    public boolean setGroupEnabled(String groupName, boolean enable) {
        boolean result = false;
        COSArray ocgs = getOCGs();
        for (COSBase o : ocgs) {
            COSDictionary ocg = toDictionary(o);
            String name = ocg.getString(COSName.NAME);
            if (groupName.equals(name) && setGroupEnabled(new PDOptionalContentGroup(ocg), enable)) {
                result = true;
            }
        }
        return result;
    }

    public boolean setGroupEnabled(PDOptionalContentGroup group, boolean enable) {
        COSArray on;
        COSArray off;
        COSDictionary d = getD();
        COSBase base = d.getDictionaryObject(COSName.ON);
        if (!(base instanceof COSArray)) {
            on = new COSArray();
            d.setItem(COSName.ON, (COSBase) on);
        } else {
            on = (COSArray) base;
        }
        COSBase base2 = d.getDictionaryObject(COSName.OFF);
        if (!(base2 instanceof COSArray)) {
            off = new COSArray();
            d.setItem(COSName.OFF, (COSBase) off);
        } else {
            off = (COSArray) base2;
        }
        boolean found = false;
        if (enable) {
            for (COSBase o : off) {
                COSDictionary groupDictionary = toDictionary(o);
                if (groupDictionary == group.getCOSObject()) {
                    off.remove(o);
                    on.add(o);
                    found = true;
                    break;
                }
            }
        } else {
            for (COSBase o2 : on) {
                COSDictionary groupDictionary2 = toDictionary(o2);
                if (groupDictionary2 == group.getCOSObject()) {
                    on.remove(o2);
                    off.add(o2);
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            if (enable) {
                on.add((COSBase) group.getCOSObject());
            } else {
                off.add((COSBase) group.getCOSObject());
            }
        }
        return found;
    }
}
