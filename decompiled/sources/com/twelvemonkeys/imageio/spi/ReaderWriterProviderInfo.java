package com.twelvemonkeys.imageio.spi;

import com.twelvemonkeys.lang.Validate;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/spi/ReaderWriterProviderInfo.class */
public abstract class ReaderWriterProviderInfo extends ProviderInfo {
    private final String[] formatNames;
    private final String[] suffixes;
    private final String[] mimeTypes;
    private final String readerClassName;
    private final String[] readerSpiClassNames;
    private final Class<?>[] inputTypes;
    private final String writerClassName;
    private final String[] writerSpiClassNames;
    private final Class<?>[] outputTypes;
    private final boolean supportsStandardStreamMetadata;
    private final String nativeStreamMetadataFormatName;
    private final String nativeStreamMetadataFormatClassName;
    private final String[] extraStreamMetadataFormatNames;
    private final String[] extraStreamMetadataFormatClassNames;
    private final boolean supportsStandardImageMetadata;
    private final String nativeImageMetadataFormatName;
    private final String nativeImageMetadataFormatClassName;
    private final String[] extraImageMetadataFormatNames;
    private final String[] extraImageMetadataFormatClassNames;

    protected ReaderWriterProviderInfo(Class<? extends ReaderWriterProviderInfo> cls, String[] strArr, String[] strArr2, String[] strArr3, String str, String[] strArr4, String str2, String[] strArr5, boolean z, String str3, String str4, String[] strArr6, String[] strArr7, boolean z2, String str5, String str6, String[] strArr8, String[] strArr9) {
        super(((Class) Validate.notNull(cls)).getPackage());
        this.inputTypes = new Class[]{ImageInputStream.class};
        this.outputTypes = new Class[]{ImageOutputStream.class};
        this.formatNames = strArr;
        this.suffixes = strArr2;
        this.mimeTypes = strArr3;
        this.readerClassName = str;
        this.readerSpiClassNames = strArr4;
        this.writerClassName = str2;
        this.writerSpiClassNames = strArr5;
        this.supportsStandardStreamMetadata = z;
        this.nativeStreamMetadataFormatName = str3;
        this.nativeStreamMetadataFormatClassName = str4;
        this.extraStreamMetadataFormatNames = strArr6;
        this.extraStreamMetadataFormatClassNames = strArr7;
        this.supportsStandardImageMetadata = z2;
        this.nativeImageMetadataFormatName = str5;
        this.nativeImageMetadataFormatClassName = str6;
        this.extraImageMetadataFormatNames = strArr8;
        this.extraImageMetadataFormatClassNames = strArr9;
    }

    public String[] formatNames() {
        return this.formatNames;
    }

    public String[] suffixes() {
        return this.suffixes;
    }

    public String[] mimeTypes() {
        return this.mimeTypes;
    }

    public String readerClassName() {
        return this.readerClassName;
    }

    public String[] readerSpiClassNames() {
        return this.readerSpiClassNames;
    }

    public Class[] inputTypes() {
        return this.inputTypes;
    }

    public String writerClassName() {
        return this.writerClassName;
    }

    public String[] writerSpiClassNames() {
        return this.writerSpiClassNames;
    }

    public Class[] outputTypes() {
        return this.outputTypes;
    }

    public boolean supportsStandardStreamMetadataFormat() {
        return this.supportsStandardStreamMetadata;
    }

    public String nativeStreamMetadataFormatName() {
        return this.nativeStreamMetadataFormatName;
    }

    public String nativeStreamMetadataFormatClassName() {
        return this.nativeStreamMetadataFormatClassName;
    }

    public String[] extraStreamMetadataFormatNames() {
        return this.extraStreamMetadataFormatNames;
    }

    public String[] extraStreamMetadataFormatClassNames() {
        return this.extraStreamMetadataFormatClassNames;
    }

    public boolean supportsStandardImageMetadataFormat() {
        return this.supportsStandardImageMetadata;
    }

    public String nativeImageMetadataFormatName() {
        return this.nativeImageMetadataFormatName;
    }

    public String nativeImageMetadataFormatClassName() {
        return this.nativeImageMetadataFormatClassName;
    }

    public String[] extraImageMetadataFormatNames() {
        return this.extraImageMetadataFormatNames;
    }

    public String[] extraImageMetadataFormatClassNames() {
        return this.extraImageMetadataFormatClassNames;
    }
}
