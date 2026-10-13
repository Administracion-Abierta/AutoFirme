package jakarta.xml.bind.attachment;

import jakarta.activation.DataHandler;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/attachment/AttachmentUnmarshaller.class */
public abstract class AttachmentUnmarshaller {
    public abstract DataHandler getAttachmentAsDataHandler(String str);

    public abstract byte[] getAttachmentAsByteArray(String str);

    public boolean isXOPPackage() {
        return false;
    }
}
