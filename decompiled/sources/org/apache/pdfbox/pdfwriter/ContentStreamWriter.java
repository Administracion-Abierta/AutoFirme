package org.apache.pdfbox.pdfwriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.util.Charsets;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfwriter/ContentStreamWriter.class */
public class ContentStreamWriter {
    private final OutputStream output;
    public static final byte[] SPACE = {32};
    public static final byte[] EOL = {10};

    public ContentStreamWriter(OutputStream out) {
        this.output = out;
    }

    public void writeToken(COSBase base) throws IOException {
        writeObject(base);
    }

    public void writeToken(Operator op) throws IOException {
        writeObject(op);
    }

    public void writeTokens(Object... tokens) throws IOException {
        for (Object token : tokens) {
            writeObject(token);
        }
        this.output.write("\n".getBytes(Charsets.US_ASCII));
    }

    public void writeTokens(List<?> tokens) throws IOException {
        for (Object token : tokens) {
            writeObject(token);
        }
    }

    private void writeObject(Object o) throws IOException {
        if (o instanceof COSString) {
            COSWriter.writeString((COSString) o, this.output);
            this.output.write(SPACE);
            return;
        }
        if (o instanceof COSFloat) {
            ((COSFloat) o).writePDF(this.output);
            this.output.write(SPACE);
            return;
        }
        if (o instanceof COSInteger) {
            ((COSInteger) o).writePDF(this.output);
            this.output.write(SPACE);
            return;
        }
        if (o instanceof COSBoolean) {
            ((COSBoolean) o).writePDF(this.output);
            this.output.write(SPACE);
            return;
        }
        if (o instanceof COSName) {
            ((COSName) o).writePDF(this.output);
            this.output.write(SPACE);
            return;
        }
        if (o instanceof COSArray) {
            COSArray array = (COSArray) o;
            this.output.write(COSWriter.ARRAY_OPEN);
            for (int i = 0; i < array.size(); i++) {
                writeObject(array.get(i));
            }
            this.output.write(COSWriter.ARRAY_CLOSE);
            this.output.write(SPACE);
            return;
        }
        if (o instanceof COSDictionary) {
            COSDictionary obj = (COSDictionary) o;
            this.output.write(COSWriter.DICT_OPEN);
            for (Map.Entry<COSName, COSBase> entry : obj.entrySet()) {
                if (entry.getValue() != null) {
                    writeObject(entry.getKey());
                    writeObject(entry.getValue());
                }
            }
            this.output.write(COSWriter.DICT_CLOSE);
            this.output.write(SPACE);
            return;
        }
        if (o instanceof Operator) {
            Operator op = (Operator) o;
            if (op.getName().equals(OperatorName.BEGIN_INLINE_IMAGE)) {
                this.output.write(OperatorName.BEGIN_INLINE_IMAGE.getBytes(Charsets.ISO_8859_1));
                this.output.write(EOL);
                COSDictionary dic = op.getImageParameters();
                for (COSName key : dic.keySet()) {
                    Object value = dic.getDictionaryObject(key);
                    key.writePDF(this.output);
                    this.output.write(SPACE);
                    writeObject(value);
                    this.output.write(EOL);
                }
                this.output.write("ID".getBytes(Charsets.ISO_8859_1));
                this.output.write(EOL);
                this.output.write(op.getImageData());
                this.output.write(EOL);
                this.output.write(OperatorName.END_INLINE_IMAGE.getBytes(Charsets.ISO_8859_1));
                this.output.write(EOL);
                return;
            }
            this.output.write(op.getName().getBytes(Charsets.ISO_8859_1));
            this.output.write(EOL);
            return;
        }
        if (o instanceof COSNull) {
            this.output.write("null".getBytes(Charsets.ISO_8859_1));
            this.output.write(SPACE);
            return;
        }
        throw new IOException("Error:Unknown type in content stream:" + o);
    }
}
