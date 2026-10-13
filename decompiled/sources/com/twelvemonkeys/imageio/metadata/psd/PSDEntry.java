package com.twelvemonkeys.imageio.metadata.psd;

import com.twelvemonkeys.imageio.metadata.AbstractEntry;
import com.twelvemonkeys.lang.StringUtil;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/psd/PSDEntry.class */
class PSDEntry extends AbstractEntry {
    private final String name;

    public PSDEntry(int i, String str, Object obj) {
        super(Integer.valueOf(i), obj);
        this.name = StringUtil.isEmpty(str) ? null : str;
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractEntry
    protected String getNativeIdentifier() {
        return String.format("0x%04x", (Integer) getIdentifier());
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractEntry, com.twelvemonkeys.imageio.metadata.Entry
    public String getFieldName() {
        for (Class cls : new Class[]{getPSDClass()}) {
            for (Field field : cls.getDeclaredFields()) {
                try {
                    if (field.getType() == Integer.TYPE && field.getName().startsWith("RES_")) {
                        field.setAccessible(true);
                        if (field.get(null).equals(getIdentifier())) {
                            String strLispToCamel = StringUtil.lispToCamel(field.getName().substring(4).replace("_", "-").toLowerCase(), true);
                            return this.name != null ? strLispToCamel + ": " + this.name : strLispToCamel;
                        }
                    }
                } catch (IllegalAccessException e) {
                }
            }
        }
        return this.name;
    }

    private Class<?> getPSDClass() {
        try {
            return Class.forName("com.twelvemonkeys.imageio.plugins.psd.PSD");
        } catch (ClassNotFoundException e) {
            return PSD.class;
        }
    }
}
