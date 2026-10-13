package com.twelvemonkeys.imageio.metadata;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/Entry.class */
public interface Entry {
    Object getIdentifier();

    String getFieldName();

    Object getValue();

    String getValueAsString();

    String getTypeName();

    int valueCount();
}
