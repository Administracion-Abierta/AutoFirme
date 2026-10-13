package com.aowagie.text.pdf;

import com.aowagie.text.pdf.collection.PdfTargetDictionary;
import es.gob.afirma.signvalidation.ISignatureFormatDetector;
import java.io.IOException;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfAction.class */
public class PdfAction extends PdfDictionary {
    private static final int FIRSTPAGE = 1;
    private static final int PREVPAGE = 2;
    private static final int NEXTPAGE = 3;
    private static final int LASTPAGE = 4;
    private static final int PRINTDIALOG = 5;

    public PdfAction() {
    }

    public PdfAction(URL url) {
        this(url.toExternalForm());
    }

    public PdfAction(String url) {
        this(url, false);
    }

    private PdfAction(String url, boolean isMap) {
        put(PdfName.S, PdfName.URI);
        put(PdfName.URI, new PdfString(url));
        if (isMap) {
            put(PdfName.ISMAP, PdfBoolean.PDFTRUE);
        }
    }

    PdfAction(PdfIndirectReference destination) {
        put(PdfName.S, PdfName.GOTO);
        put(PdfName.D, destination);
    }

    public PdfAction(String filename, String name) {
        put(PdfName.S, PdfName.GOTOR);
        put(PdfName.F, new PdfString(filename));
        put(PdfName.D, new PdfString(name));
    }

    public PdfAction(String filename, int page) {
        put(PdfName.S, PdfName.GOTOR);
        put(PdfName.F, new PdfString(filename));
        put(PdfName.D, new PdfLiteral("[" + (page - 1) + " /FitH 10000]"));
    }

    public PdfAction(int named) {
        put(PdfName.S, PdfName.NAMED);
        switch (named) {
            case 1:
                put(PdfName.N, PdfName.FIRSTPAGE);
                return;
            case 2:
                put(PdfName.N, PdfName.PREVPAGE);
                return;
            case 3:
                put(PdfName.N, PdfName.NEXTPAGE);
                return;
            case 4:
                put(PdfName.N, PdfName.LASTPAGE);
                return;
            case 5:
                put(PdfName.S, PdfName.JAVASCRIPT);
                put(PdfName.JS, new PdfString("this.print(true);\r"));
                return;
            default:
                throw new RuntimeException("Invalid named action.");
        }
    }

    public PdfAction(String application, String parameters, String operation, String defaultDir) {
        put(PdfName.S, PdfName.LAUNCH);
        if (parameters == null && operation == null && defaultDir == null) {
            put(PdfName.F, new PdfString(application));
            return;
        }
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.F, new PdfString(application));
        if (parameters != null) {
            dic.put(PdfName.P, new PdfString(parameters));
        }
        if (operation != null) {
            dic.put(PdfName.O, new PdfString(operation));
        }
        if (defaultDir != null) {
            dic.put(PdfName.D, new PdfString(defaultDir));
        }
        put(PdfName.WIN, dic);
    }

    static PdfAction rendition(String file, PdfFileSpecification fs, String mimeType, PdfIndirectReference ref) throws IOException {
        PdfAction js = new PdfAction();
        js.put(PdfName.S, PdfName.RENDITION);
        js.put(PdfName.R, new PdfRendition(file, fs, mimeType));
        js.put(new PdfName("OP"), new PdfNumber(0));
        js.put(new PdfName("AN"), ref);
        return js;
    }

    static PdfAction javaScript(String code, PdfWriter writer, boolean unicode) {
        PdfAction js = new PdfAction();
        js.put(PdfName.S, PdfName.JAVASCRIPT);
        if (unicode && code.length() < 50) {
            js.put(PdfName.JS, new PdfString(code, PdfObject.TEXT_UNICODE));
        } else if (!unicode && code.length() < 100) {
            js.put(PdfName.JS, new PdfString(code));
        } else {
            try {
                byte[] b = PdfEncodings.convertToBytes(code, unicode ? PdfObject.TEXT_UNICODE : ISignatureFormatDetector.FORMAT_PDF);
                PdfStream stream = new PdfStream(b);
                stream.flateCompress(writer.getCompressionLevel());
                js.put(PdfName.JS, writer.addToBody(stream).getIndirectReference());
            } catch (Exception e) {
                js.put(PdfName.JS, new PdfString(code));
            }
        }
        return js;
    }

    private static PdfAction createHide(PdfObject obj, boolean hide) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.HIDE);
        action.put(PdfName.T, obj);
        if (!hide) {
            action.put(PdfName.H, PdfBoolean.PDFFALSE);
        }
        return action;
    }

    private static PdfArray buildArray(Object[] names) {
        PdfArray array = new PdfArray();
        for (Object obj : names) {
            if (obj instanceof String) {
                array.add(new PdfString((String) obj));
            } else if (obj instanceof PdfAnnotation) {
                array.add(((PdfAnnotation) obj).getIndirectReference());
            } else {
                throw new RuntimeException("The array must contain String or PdfAnnotation.");
            }
        }
        return array;
    }

    private static PdfAction gotoEmbedded(String filename, PdfTargetDictionary target, PdfObject dest, boolean newWindow) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.GOTOE);
        action.put(PdfName.T, target);
        action.put(PdfName.D, dest);
        action.put(PdfName.NEWWINDOW, new PdfBoolean(newWindow));
        if (filename != null) {
            action.put(PdfName.F, new PdfString(filename));
        }
        return action;
    }
}
