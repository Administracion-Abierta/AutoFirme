package es.gob.jmulticard.card.dnie;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/Dnie3r2Cwa14890Constants.class */
abstract class Dnie3r2Cwa14890Constants extends Dnie3Cwa14890Constants {
    private static final byte[] C_CV_CA = {127, 33, -127, -50, 95, 55, -127, -128, 122, -96, 108, -106, 94, -113, -78, 25, 97, -49, -44, 73, -48, -101, -99, -81, 3, 4, 115, 1, 21, 105, 112, -73, 115, -15, -100, 64, -15, 39, -45, 56, -29, -63, 53, -21, 33, 32, 86, 109, -58, -7, -9, 69, -1, -72, -8, -30, -74, 30, -24, 22, 111, -3, 6, -46, -116, -76, -116, 21, 42, 31, -92, -9, -23, -10, 9, -41, 82, 118, 51, 28, -73, 0, -72, 78, 54, -84, -118, 10, 119, 116, 70, -116, 60, -13, -47, 71, -92, -100, -105, 110, 23, -85, 2, -38, 3, -22, 74, -63, 81, 119, 126, -33, -68, 53, -62, 125, 86, -5, -90, -123, 117, 110, -42, 82, -123, 29, -3, -25, 1, -65, -121, 73, -110, -35, 77, -24, 95, 56, 61, 51, -29, -43, 42, 75, 9, 64, -29, -112, -51, 26, 100, 31, -22, 46, -100, -35, 121, -45, -121, 45, -42, -59, 8, -43, -17, 35, -100, -80, 126, -75, 85, 104, -50, 24, -117, 101, 19, -84, -72, -124, 20, -55, -83, -9, -90, 78, 44, -64, -77, 20, -47, 39, 84, -82, -18, 103, 0, 1, 0, 1, 66, 8, 101, 115, 82, 68, 73, 98, 0, 24};
    private static final byte[] CHR_C_CV_CA = {101, 115, 83, 68, 73, 98, 0, 24};

    Dnie3r2Cwa14890Constants() {
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public byte[] getCCvCa() {
        return C_CV_CA;
    }

    @Override // es.gob.jmulticard.card.cwa14890.Cwa14890PublicConstants
    public byte[] getChrCCvCa() {
        return CHR_C_CV_CA;
    }
}
