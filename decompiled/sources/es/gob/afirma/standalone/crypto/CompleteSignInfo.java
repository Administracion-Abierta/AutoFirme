package es.gob.afirma.standalone.crypto;

import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSimpleSignInfo;
import es.gob.afirma.core.signers.AOTimestampInfo;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/crypto/CompleteSignInfo.class */
public final class CompleteSignInfo {
    private byte[] signData = null;
    private byte[] data = null;
    private AOSignInfo signInfo = null;
    private AOTreeModel signsTree = null;
    private List<AOTimestampInfo> timestampsInfo = null;
    private AOTreeModel timestampsTree = null;

    public AOTreeModel getTimestampsTree() {
        if (this.timestampsTree == null) {
            AOTreeNode aOTreeNode = new AOTreeNode("Datos");
            if (this.timestampsInfo != null && !this.timestampsInfo.isEmpty()) {
                for (AOTimestampInfo aOTimestampInfo : this.timestampsInfo) {
                    aOTreeNode.add(new AOTreeNode(new AOSimpleSignInfo(new X509Certificate[]{aOTimestampInfo.getIssuer()}, aOTimestampInfo.getDate())));
                }
            }
            this.timestampsTree = new AOTreeModel(aOTreeNode);
        }
        return this.timestampsTree;
    }

    public List<AOTimestampInfo> getTimestampsInfo() {
        return this.timestampsInfo;
    }

    public void setTimestampsInfo(List<AOTimestampInfo> list) {
        this.timestampsInfo = list;
    }

    public byte[] getSignData() {
        if (this.signData == null) {
            return null;
        }
        return (byte[]) this.signData.clone();
    }

    public void setSignData(byte[] bArr) {
        this.signData = (byte[]) bArr.clone();
    }

    public AOSignInfo getSignInfo() {
        return this.signInfo;
    }

    public void setSignInfo(AOSignInfo aOSignInfo) {
        this.signInfo = aOSignInfo;
    }

    public AOTreeModel getSignsTree() {
        return this.signsTree;
    }

    public void setSignsTree(AOTreeModel aOTreeModel) {
        this.signsTree = aOTreeModel;
    }

    public byte[] getData() {
        if (this.data == null) {
            return null;
        }
        return (byte[]) this.data.clone();
    }

    public void setData(byte[] bArr) {
        if (bArr != null) {
            if (Base64.isBase64(bArr)) {
                try {
                    byte[] bArrDecode = Base64.decode(bArr, 0, bArr.length, false);
                    String extension = new MimeHelper(bArrDecode).getExtension();
                    if (extension != null && !extension.isEmpty()) {
                        this.data = bArrDecode;
                        return;
                    }
                } catch (IOException e) {
                }
            }
            this.data = (byte[]) bArr.clone();
            return;
        }
        this.data = null;
    }
}
