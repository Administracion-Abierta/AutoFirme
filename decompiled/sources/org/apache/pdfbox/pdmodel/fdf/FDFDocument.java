package org.apache.pdfbox.pdmodel.fdf;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfparser.FDFParser;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFDocument.class */
public class FDFDocument implements Closeable {
    private COSDocument document;

    public FDFDocument() {
        this.document = new COSDocument();
        this.document.setVersion(1.2f);
        this.document.setTrailer(new COSDictionary());
        FDFCatalog catalog = new FDFCatalog();
        setCatalog(catalog);
    }

    public FDFDocument(COSDocument doc) {
        this.document = doc;
    }

    public FDFDocument(Document doc) throws IOException {
        this();
        Element xfdf = doc.getDocumentElement();
        if (!xfdf.getNodeName().equals("xfdf")) {
            throw new IOException("Error while importing xfdf document, root should be 'xfdf' and not '" + xfdf.getNodeName() + OperatorName.SHOW_TEXT_LINE);
        }
        FDFCatalog cat = new FDFCatalog(xfdf);
        setCatalog(cat);
    }

    public void writeXML(Writer output) throws IOException {
        output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        output.write("<xfdf xmlns=\"http://ns.adobe.com/xfdf/\" xml:space=\"preserve\">\n");
        getCatalog().writeXML(output);
        output.write("</xfdf>\n");
    }

    public COSDocument getDocument() {
        return this.document;
    }

    public FDFCatalog getCatalog() {
        FDFCatalog retval;
        COSDictionary trailer = this.document.getTrailer();
        COSDictionary root = trailer.getCOSDictionary(COSName.ROOT);
        if (root == null) {
            retval = new FDFCatalog();
            setCatalog(retval);
        } else {
            retval = new FDFCatalog(root);
        }
        return retval;
    }

    public void setCatalog(FDFCatalog cat) {
        COSDictionary trailer = this.document.getTrailer();
        trailer.setItem(COSName.ROOT, cat);
    }

    public static FDFDocument load(String filename) throws IOException {
        FDFParser parser = new FDFParser(filename);
        parser.parse();
        return new FDFDocument(parser.getDocument());
    }

    public static FDFDocument load(File file) throws IOException {
        FDFParser parser = new FDFParser(file);
        parser.parse();
        return new FDFDocument(parser.getDocument());
    }

    public static FDFDocument load(InputStream input) throws IOException {
        FDFParser parser = new FDFParser(input);
        parser.parse();
        return new FDFDocument(parser.getDocument());
    }

    public static FDFDocument loadXFDF(String filename) throws IOException {
        return loadXFDF(new BufferedInputStream(new FileInputStream(filename)));
    }

    public static FDFDocument loadXFDF(File file) throws IOException {
        return loadXFDF(new BufferedInputStream(new FileInputStream(file)));
    }

    public static FDFDocument loadXFDF(InputStream input) throws IOException {
        return new FDFDocument(org.apache.pdfbox.util.XMLUtil.parse(input));
    }

    public void save(File fileName) throws IOException {
        save(new FileOutputStream(fileName));
    }

    public void save(String fileName) throws IOException {
        save(new FileOutputStream(fileName));
    }

    public void save(OutputStream output) throws IOException {
        COSWriter writer = null;
        try {
            COSWriter writer2 = new COSWriter(output);
            writer2.write(this);
            writer2.close();
            if (writer2 != null) {
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void saveXFDF(File fileName) throws IOException {
        saveXFDF(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8")));
    }

    public void saveXFDF(String fileName) throws IOException {
        saveXFDF(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8")));
    }

    public void saveXFDF(Writer output) throws IOException {
        try {
            writeXML(output);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.document.close();
    }
}
