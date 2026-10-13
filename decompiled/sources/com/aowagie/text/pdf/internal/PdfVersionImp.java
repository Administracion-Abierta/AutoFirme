package com.aowagie.text.pdf.internal;

import com.aowagie.text.DocWriter;
import com.aowagie.text.pdf.OutputStreamCounter;
import com.aowagie.text.pdf.PdfDeveloperExtension;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfWriter;
import com.aowagie.text.pdf.interfaces.PdfVersion;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/internal/PdfVersionImp.class */
public class PdfVersionImp implements PdfVersion {
    private static final byte[][] HEADER = {DocWriter.getISOBytes("\n"), DocWriter.getISOBytes("%PDF-"), DocWriter.getISOBytes("\n%âãÏÓ\n")};
    private boolean headerWasWritten = false;
    private boolean appendmode = false;
    private char header_version = '4';
    private PdfName catalog_version = null;
    private PdfDictionary extensions = null;

    @Override // com.aowagie.text.pdf.interfaces.PdfVersion
    public void setPdfVersion(char version) {
        if (this.headerWasWritten || this.appendmode) {
            setPdfVersion(getVersionAsName(version));
        } else {
            this.header_version = version;
        }
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfVersion
    public void setAtLeastPdfVersion(char version) {
        if (version > this.header_version) {
            setPdfVersion(version);
        }
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfVersion
    public void setPdfVersion(PdfName version) {
        if (this.catalog_version == null || this.catalog_version.compareTo(version) < 0) {
            this.catalog_version = version;
        }
    }

    public void setAppendmode(boolean appendmode) {
        this.appendmode = appendmode;
    }

    public void writeHeader(OutputStreamCounter os) throws IOException {
        if (this.appendmode) {
            os.write(HEADER[0]);
            return;
        }
        os.write(HEADER[1]);
        os.write(getVersionAsByteArray(this.header_version));
        os.write(HEADER[2]);
        this.headerWasWritten = true;
    }

    public static PdfName getVersionAsName(char version) {
        switch (version) {
            case '2':
                return PdfWriter.PDF_VERSION_1_2;
            case '3':
                return PdfWriter.PDF_VERSION_1_3;
            case '4':
                return PdfWriter.PDF_VERSION_1_4;
            case '5':
                return PdfWriter.PDF_VERSION_1_5;
            case '6':
                return PdfWriter.PDF_VERSION_1_6;
            case '7':
                return PdfWriter.PDF_VERSION_1_7;
            default:
                return PdfWriter.PDF_VERSION_1_4;
        }
    }

    private byte[] getVersionAsByteArray(char version) {
        return DocWriter.getISOBytes(getVersionAsName(version).toString().substring(1));
    }

    public void addToCatalog(PdfDictionary catalog) {
        if (this.catalog_version != null) {
            catalog.put(PdfName.VERSION, this.catalog_version);
        }
        if (this.extensions != null) {
            catalog.put(PdfName.EXTENSIONS, this.extensions);
        }
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfVersion
    public void addDeveloperExtension(PdfDeveloperExtension de) {
        if (this.extensions == null) {
            this.extensions = new PdfDictionary();
        } else {
            PdfDictionary extension = this.extensions.getAsDict(de.getPrefix());
            if (extension != null) {
                int diff = de.getBaseversion().compareTo(extension.getAsName(PdfName.BASEVERSION));
                if (diff < 0) {
                    return;
                }
                int diff2 = de.getExtensionLevel() - extension.getAsNumber(PdfName.EXTENSIONLEVEL).intValue();
                if (diff2 <= 0) {
                    return;
                }
            }
        }
        this.extensions.put(de.getPrefix(), de.getDeveloperExtensions());
    }
}
