package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDStructureElementNameTreeNode;
import org.apache.pdfbox.pdmodel.common.COSDictionaryMap;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.PDNumberTreeNode;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/logicalstructure/PDStructureTreeRoot.class */
public class PDStructureTreeRoot extends PDStructureNode {
    private static final Log LOG = LogFactory.getLog(PDStructureTreeRoot.class);
    private static final String TYPE = "StructTreeRoot";

    public PDStructureTreeRoot() {
        super(TYPE);
    }

    public PDStructureTreeRoot(COSDictionary dic) {
        super(dic);
    }

    @Deprecated
    public COSArray getKArray() {
        COSBase k = getCOSObject().getDictionaryObject(COSName.K);
        if (k instanceof COSDictionary) {
            COSDictionary kdict = (COSDictionary) k;
            COSBase k2 = kdict.getDictionaryObject(COSName.K);
            if (k2 instanceof COSArray) {
                return (COSArray) k2;
            }
            return null;
        }
        if (k instanceof COSArray) {
            return (COSArray) k;
        }
        return null;
    }

    public COSBase getK() {
        return getCOSObject().getDictionaryObject(COSName.K);
    }

    public void setK(COSBase k) {
        getCOSObject().setItem(COSName.K, k);
    }

    public PDNameTreeNode<PDStructureElement> getIDTree() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.ID_TREE);
        if (base instanceof COSDictionary) {
            return new PDStructureElementNameTreeNode((COSDictionary) base);
        }
        return null;
    }

    public void setIDTree(PDNameTreeNode<PDStructureElement> idTree) {
        getCOSObject().setItem(COSName.ID_TREE, idTree);
    }

    public PDNumberTreeNode getParentTree() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.PARENT_TREE);
        if (base instanceof COSDictionary) {
            return new PDNumberTreeNode((COSDictionary) base, PDParentTreeValue.class);
        }
        return null;
    }

    public void setParentTree(PDNumberTreeNode parentTree) {
        getCOSObject().setItem(COSName.PARENT_TREE, parentTree);
    }

    public int getParentTreeNextKey() {
        return getCOSObject().getInt(COSName.PARENT_TREE_NEXT_KEY);
    }

    public void setParentTreeNextKey(int parentTreeNextkey) {
        getCOSObject().setInt(COSName.PARENT_TREE_NEXT_KEY, parentTreeNextkey);
    }

    public Map<String, Object> getRoleMap() {
        COSBase rm = getCOSObject().getDictionaryObject(COSName.ROLE_MAP);
        if (rm instanceof COSDictionary) {
            try {
                return COSDictionaryMap.convertBasicTypesToMap((COSDictionary) rm);
            } catch (IOException e) {
                LOG.error(e, e);
            }
        }
        return new HashMap();
    }

    public void setRoleMap(Map<String, String> roleMap) {
        COSDictionary rmDic = new COSDictionary();
        for (Map.Entry<String, String> entry : roleMap.entrySet()) {
            rmDic.setName(entry.getKey(), entry.getValue());
        }
        getCOSObject().setItem(COSName.ROLE_MAP, (COSBase) rmDic);
    }
}
