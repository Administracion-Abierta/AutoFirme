package es.gob.jmulticard.apdu.iso7816four;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/MseSetAuthenticationKeyApduCommand.class */
public final class MseSetAuthenticationKeyApduCommand extends MseSetAuthenticationTemplateApduCommand {
    public MseSetAuthenticationKeyApduCommand(byte cla, byte[] publicKeyFileId, byte[] privateKeyRef) {
        super(cla, buidData(publicKeyFileId, privateKeyRef));
    }

    private static byte[] buidData(byte[] publicKeyFileId, byte[] privateKeyRef) {
        byte[] publicKeyFileIdCompleted = new byte[12];
        System.arraycopy(publicKeyFileId, 0, publicKeyFileIdCompleted, publicKeyFileIdCompleted.length - publicKeyFileId.length, publicKeyFileId.length);
        for (int i = 0; i < publicKeyFileIdCompleted.length - publicKeyFileId.length; i++) {
            publicKeyFileIdCompleted[i] = 0;
        }
        byte[] ret = new byte[publicKeyFileIdCompleted.length + privateKeyRef.length + 4];
        ret[0] = -125;
        ret[1] = (byte) publicKeyFileIdCompleted.length;
        System.arraycopy(publicKeyFileIdCompleted, 0, ret, 2, publicKeyFileIdCompleted.length);
        int idx = 1 + publicKeyFileIdCompleted.length + 1;
        ret[idx] = -124;
        int idx2 = idx + 1;
        ret[idx2] = (byte) privateKeyRef.length;
        System.arraycopy(privateKeyRef, 0, ret, idx2 + 1, privateKeyRef.length);
        return ret;
    }
}
