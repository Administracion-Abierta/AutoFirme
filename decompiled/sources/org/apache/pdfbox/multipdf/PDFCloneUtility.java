package org.apache.pdfbox.multipdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/multipdf/PDFCloneUtility.class */
public class PDFCloneUtility {
    private final PDDocument destination;
    private final Map<Object, COSBase> clonedVersion = new HashMap();
    private final Set<COSBase> clonedValues = new HashSet();

    public PDFCloneUtility(PDDocument dest) {
        this.destination = dest;
    }

    public PDDocument getDestination() {
        return this.destination;
    }

    public COSBase cloneForNewDocument(Object base) throws IOException {
        COSBase retval;
        if (base == null) {
            return null;
        }
        COSBase retval2 = this.clonedVersion.get(base);
        if (retval2 != null) {
            return retval2;
        }
        if ((base instanceof COSBase) && this.clonedValues.contains(base)) {
            return (COSBase) base;
        }
        if (base instanceof List) {
            COSArray array = new COSArray();
            List<?> list = (List) base;
            for (Object obj : list) {
                array.add(cloneForNewDocument(obj));
            }
            retval = array;
        } else if ((base instanceof COSObjectable) && !(base instanceof COSBase)) {
            retval = cloneForNewDocument(((COSObjectable) base).getCOSObject());
        } else if (base instanceof COSObject) {
            COSObject object = (COSObject) base;
            retval = cloneForNewDocument(object.getObject());
        } else if (base instanceof COSArray) {
            COSArray newArray = new COSArray();
            COSArray array2 = (COSArray) base;
            for (int i = 0; i < array2.size(); i++) {
                newArray.add(cloneForNewDocument(array2.get(i)));
            }
            retval = newArray;
        } else if (base instanceof COSStream) {
            COSStream originalStream = (COSStream) base;
            COSStream stream = this.destination.getDocument().createCOSStream();
            OutputStream output = stream.createRawOutputStream();
            InputStream input = originalStream.createRawInputStream();
            IOUtils.copy(input, output);
            input.close();
            output.close();
            this.clonedVersion.put(base, stream);
            for (Map.Entry<COSName, COSBase> entry : originalStream.entrySet()) {
                stream.setItem(entry.getKey(), cloneForNewDocument(entry.getValue()));
            }
            retval = stream;
        } else if (base instanceof COSDictionary) {
            COSDictionary dic = (COSDictionary) base;
            retval = new COSDictionary();
            this.clonedVersion.put(base, retval);
            for (Map.Entry<COSName, COSBase> entry2 : dic.entrySet()) {
                ((COSDictionary) retval).setItem(entry2.getKey(), cloneForNewDocument(entry2.getValue()));
            }
        } else {
            retval = (COSBase) base;
        }
        this.clonedVersion.put(base, retval);
        this.clonedValues.add(retval);
        return retval;
    }

    /* JADX WARN: Type inference incomplete: some casts might be missing */
    public void cloneMerge(COSObjectable cOSObjectable, COSObjectable cOSObjectable2) throws IOException {
        if (cOSObjectable == null || cOSObjectable == cOSObjectable2) {
            return;
        }
        COSBase cOSBase = this.clonedVersion.get(cOSObjectable);
        if (cOSBase != null) {
            return;
        }
        if (!(cOSObjectable instanceof COSBase)) {
            cloneMerge(cOSObjectable.getCOSObject(), cOSObjectable2.getCOSObject());
        } else if (cOSObjectable instanceof COSObject) {
            if (cOSObjectable2 instanceof COSObject) {
                cloneMerge(((COSObject) cOSObjectable).getObject(), ((COSObject) cOSObjectable2).getObject());
            } else if ((cOSObjectable2 instanceof COSDictionary) || (cOSObjectable2 instanceof COSArray)) {
                cloneMerge(((COSObject) cOSObjectable).getObject(), cOSObjectable2);
            }
        } else if (cOSObjectable instanceof COSArray) {
            if (cOSObjectable2 instanceof COSObject) {
                cloneMerge(cOSObjectable, ((COSObject) cOSObjectable2).getObject());
            } else {
                COSArray cOSArray = (COSArray) cOSObjectable;
                for (int i = 0; i < cOSArray.size(); i++) {
                    ((COSArray) cOSObjectable2).add(cloneForNewDocument(cOSArray.get(i)));
                }
            }
        } else if (cOSObjectable instanceof COSStream) {
            COSStream cOSStream = (COSStream) cOSObjectable;
            COSStream cOSStreamCreateCOSStream = this.destination.getDocument().createCOSStream();
            OutputStream outputStreamCreateOutputStream = cOSStreamCreateCOSStream.createOutputStream(cOSStream.getFilters());
            IOUtils.copy(cOSStream.createInputStream(), outputStreamCreateOutputStream);
            outputStreamCreateOutputStream.close();
            this.clonedVersion.put(cOSObjectable, cOSStreamCreateCOSStream);
            for (Map.Entry<COSName, COSBase> entry : cOSStream.entrySet()) {
                cOSStreamCreateCOSStream.setItem(entry.getKey(), cloneForNewDocument(entry.getValue()));
            }
            cOSBase = cOSStreamCreateCOSStream;
        } else if (cOSObjectable instanceof COSDictionary) {
            if (cOSObjectable2 instanceof COSObject) {
                cloneMerge(cOSObjectable, ((COSObject) cOSObjectable2).getObject());
            } else {
                this.clonedVersion.put(cOSObjectable, cOSBase);
                for (Map.Entry<COSName, COSBase> entry2 : ((COSDictionary) cOSObjectable).entrySet()) {
                    COSName key = entry2.getKey();
                    COSBase value = entry2.getValue();
                    if (((COSDictionary) cOSObjectable2).getItem(key) != null) {
                        cloneMerge(value, ((COSDictionary) cOSObjectable2).getItem(key));
                    } else {
                        ((COSDictionary) cOSObjectable2).setItem(key, cloneForNewDocument(value));
                    }
                }
            }
        } else {
            cOSBase = (COSBase) cOSObjectable;
        }
        this.clonedVersion.put(cOSObjectable, cOSBase);
        this.clonedValues.add(cOSBase);
    }
}
