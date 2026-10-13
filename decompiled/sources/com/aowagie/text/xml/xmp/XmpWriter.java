package com.aowagie.text.xml.xmp;

import com.aowagie.text.pdf.PdfDate;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfObject;
import com.aowagie.text.pdf.PdfString;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/xmp/XmpWriter.class */
public class XmpWriter {
    private static final String UTF8 = StandardCharsets.UTF_8.name();
    static final String EXTRASPACE = "                                                                                                   \n";
    private final int extraSpace;
    private final OutputStreamWriter writer;
    private String about;
    static final String XPACKET_PI_BEGIN = "<?xpacket begin=\"\ufeff\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n";
    static final String XPACKET_PI_END_W = "<?xpacket end=\"w\"?>";
    private static final String XPACKET_PI_END_R = "<?xpacket end=\"r\"?>";
    private final char end = 'w';

    private XmpWriter(OutputStream os, String utfEncoding, int extraSpace) throws IOException {
        this.end = 'w';
        this.extraSpace = extraSpace;
        this.writer = new OutputStreamWriter(os, utfEncoding);
        this.writer.write(XPACKET_PI_BEGIN);
        this.writer.write("<x:xmpmeta xmlns:x=\"adobe:ns:meta/\">\n");
        this.writer.write("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n");
        this.about = "";
    }

    public XmpWriter(OutputStream os) throws IOException {
        this(os, UTF8, 20);
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void addRdfDescription(XmpSchema s) throws IOException {
        this.writer.write("<rdf:Description rdf:about=\"");
        this.writer.write(this.about);
        this.writer.write("\" ");
        this.writer.write(s.getXmlns());
        this.writer.write(">");
        this.writer.write(s.toString());
        this.writer.write("</rdf:Description>\n");
    }

    public void close() throws IOException {
        this.writer.write("</rdf:RDF>");
        this.writer.write("</x:xmpmeta>\n");
        for (int i = 0; i < this.extraSpace; i++) {
            this.writer.write(EXTRASPACE);
        }
        OutputStreamWriter outputStreamWriter = this.writer;
        getClass();
        outputStreamWriter.write(119 == 114 ? XPACKET_PI_END_R : XPACKET_PI_END_W);
        this.writer.flush();
        this.writer.close();
    }

    public XmpWriter(OutputStream os, PdfDictionary info, int PdfXConformance) throws IOException {
        this(os);
        if (info != null) {
            DublinCoreSchema dc = new DublinCoreSchema();
            PdfSchema p = new PdfSchema();
            XmpBasicSchema basic = new XmpBasicSchema();
            for (Object element : info.getKeys()) {
                PdfName key = (PdfName) element;
                PdfObject obj = info.get(key);
                if (obj != null) {
                    if (PdfName.TITLE.equals(key)) {
                        dc.addTitle(((PdfString) obj).toUnicodeString());
                    }
                    if (PdfName.AUTHOR.equals(key)) {
                        dc.addAuthor(((PdfString) obj).toUnicodeString());
                    }
                    if (PdfName.SUBJECT.equals(key)) {
                        dc.addSubject(((PdfString) obj).toUnicodeString());
                        dc.addDescription(((PdfString) obj).toUnicodeString());
                    }
                    if (PdfName.KEYWORDS.equals(key)) {
                        p.addKeywords(((PdfString) obj).toUnicodeString());
                    }
                    if (PdfName.CREATOR.equals(key)) {
                        basic.addCreatorTool(((PdfString) obj).toUnicodeString());
                    }
                    if (PdfName.PRODUCER.equals(key)) {
                        p.addProducer(((PdfString) obj).toUnicodeString());
                    }
                    if (PdfName.CREATIONDATE.equals(key)) {
                        basic.addCreateDate(((PdfDate) obj).getW3CDate());
                    }
                    if (PdfName.MODDATE.equals(key)) {
                        basic.addModDate(((PdfDate) obj).getW3CDate());
                    }
                }
            }
            if (dc.size() > 0) {
                addRdfDescription(dc);
            }
            if (p.size() > 0) {
                addRdfDescription(p);
            }
            if (basic.size() > 0) {
                addRdfDescription(basic);
            }
            if (PdfXConformance == 3 || PdfXConformance == 4) {
                PdfA1Schema a1 = new PdfA1Schema();
                if (PdfXConformance == 3) {
                    a1.addConformance("A");
                } else {
                    a1.addConformance("B");
                }
                addRdfDescription(a1);
                return;
            }
            if (PdfXConformance == 5 || PdfXConformance == 6) {
                PdfA2Schema a2 = new PdfA2Schema();
                if (PdfXConformance == 5) {
                    a2.addConformance("A");
                } else {
                    a2.addConformance("B");
                }
                addRdfDescription(a2);
            }
        }
    }
}
