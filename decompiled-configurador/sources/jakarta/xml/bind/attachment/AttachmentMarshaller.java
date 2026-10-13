package jakarta.xml.bind.attachment;

import jakarta.activation.DataHandler;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:jakarta/xml/bind/attachment/AttachmentMarshaller.class */
public abstract class AttachmentMarshaller {
    public abstract String addMtomAttachment(DataHandler dataHandler, String str, String str2);

    public abstract String addMtomAttachment(byte[] bArr, int i, int i2, String str, String str2, String str3);

    public abstract String addSwaRefAttachment(DataHandler dataHandler);

    public boolean isXOPPackage() {
        return false;
    }
}
