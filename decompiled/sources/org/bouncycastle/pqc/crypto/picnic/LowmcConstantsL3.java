package org.bouncycastle.pqc.crypto.picnic;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.bouncycastle.util.Exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/LowmcConstantsL3.class */
public class LowmcConstantsL3 extends LowmcConstants {
    LowmcConstantsL3() {
        try {
            DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(LowmcConstants.class.getResourceAsStream("lowmcL3.bin.properties")));
            this.linearMatrices = readArray(dataInputStream);
            this.roundConstants = readArray(dataInputStream);
            this.keyMatrices = readArray(dataInputStream);
            this.linearMatrices_full = readArray(dataInputStream);
            this.keyMatrices_full = readArray(dataInputStream);
            this.keyMatrices_inv = readArray(dataInputStream);
            this.linearMatrices_inv = readArray(dataInputStream);
            this.roundConstants_full = readArray(dataInputStream);
            this.LMatrix = new KMatrices(30, 192, 6, this.linearMatrices);
            this.KMatrix = new KMatrices(31, 192, 6, this.keyMatrices);
            this.RConstants = new KMatrices(30, 1, 6, this.roundConstants);
            this.LMatrix_full = new KMatrices(4, 192, 6, this.linearMatrices_full);
            this.LMatrix_inv = new KMatrices(4, 192, 6, this.linearMatrices_inv);
            this.KMatrix_full = new KMatrices(5, 192, 6, this.keyMatrices_full);
            this.KMatrix_inv = new KMatrices(1, 192, 6, this.keyMatrices_inv);
            this.RConstants_full = new KMatrices(4, 1, 6, this.roundConstants_full);
        } catch (IOException e) {
            throw Exceptions.illegalStateException("unable to load Picnic properties: " + e.getMessage(), e);
        }
    }
}
