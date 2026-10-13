package org.apache.pdfbox.pdmodel.fdf;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFPage.class */
public class FDFPage implements COSObjectable {
    private final COSDictionary page;

    public FDFPage() {
        this.page = new COSDictionary();
    }

    public FDFPage(COSDictionary p) {
        this.page = p;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.page;
    }

    public List<FDFTemplate> getTemplates() {
        List<FDFTemplate> retval = null;
        COSArray array = (COSArray) this.page.getDictionaryObject(COSName.TEMPLATES);
        if (array != null) {
            List<FDFTemplate> objects = new ArrayList<>(array.size());
            for (int i = 0; i < array.size(); i++) {
                objects.add(new FDFTemplate((COSDictionary) array.getObject(i)));
            }
            retval = new COSArrayList<>(objects, array);
        }
        return retval;
    }

    public void setTemplates(List<FDFTemplate> templates) {
        this.page.setItem(COSName.TEMPLATES, (COSBase) COSArrayList.converterToCOSArray(templates));
    }

    public FDFPageInfo getPageInfo() {
        FDFPageInfo retval = null;
        COSDictionary dict = this.page.getCOSDictionary(COSName.INFO);
        if (dict != null) {
            retval = new FDFPageInfo(dict);
        }
        return retval;
    }

    public void setPageInfo(FDFPageInfo info) {
        this.page.setItem(COSName.INFO, info);
    }
}
