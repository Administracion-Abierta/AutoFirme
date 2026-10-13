package org.bouncycastle.pqc.crypto.picnic;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.bouncycastle.util.Exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/LowmcConstantsL5.class */
public class LowmcConstantsL5 extends LowmcConstants {
    LowmcConstantsL5() {
        try {
            DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(LowmcConstants.class.getResourceAsStream("lowmcL5.bin.properties")));
            this.linearMatrices = readArray(dataInputStream);
            this.roundConstants = readArray(dataInputStream);
            this.keyMatrices = readArray(dataInputStream);
            this.linearMatrices_full = readArray(dataInputStream);
            this.keyMatrices_full = readArray(dataInputStream);
            this.keyMatrices_inv = readArray(dataInputStream);
            this.linearMatrices_inv = readArray(dataInputStream);
            this.roundConstants_full = readArray(dataInputStream);
            this.LMatrix = new KMatrices(38, 256, 8, this.linearMatrices);
            this.KMatrix = new KMatrices(39, 256, 8, this.keyMatrices);
            this.RConstants = new KMatrices(38, 1, 8, this.roundConstants);
            this.LMatrix_full = new KMatrices(4, 255, 8, this.linearMatrices_full);
            this.LMatrix_inv = new KMatrices(4, 255, 8, this.linearMatrices_inv);
            this.KMatrix_full = new KMatrices(5, 255, 8, this.keyMatrices_full);
            this.KMatrix_inv = new KMatrices(1, 255, 8, this.keyMatrices_inv);
            this.RConstants_full = new KMatrices(4, 1, 8, this.roundConstants_full);
        } catch (IOException e) {
            throw Exceptions.illegalStateException("unable to load Picnic properties: " + e.getMessage(), e);
        }
    }
}
