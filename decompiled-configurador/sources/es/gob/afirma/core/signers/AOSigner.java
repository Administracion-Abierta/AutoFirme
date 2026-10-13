package es.gob.afirma.core.signers;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.util.tree.AOTreeModel;
import java.io.IOException;
import java.util.Properties;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/AOSigner.class */
public interface AOSigner extends AOCoSigner, AOCounterSigner, AOSimpleSigner {
    AOTreeModel getSignersStructure(byte[] bArr, boolean z) throws IOException, AOInvalidFormatException;

    AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) throws IOException, AOInvalidFormatException;

    boolean isSign(byte[] bArr) throws IOException;

    boolean isSign(byte[] bArr, Properties properties) throws IOException;

    boolean isValidDataFile(byte[] bArr) throws IOException;

    String getSignedName(String str, String str2);

    byte[] getData(byte[] bArr) throws IOException, AOException;

    byte[] getData(byte[] bArr, Properties properties) throws IOException, AOException;

    AOSignInfo getSignInfo(byte[] bArr) throws IOException, AOException;

    AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws IOException, AOException;
}
