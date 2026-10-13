package es.gob.jmulticard.card.dnie;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/Dnie3r1Cwa14890Constants.class */
abstract class Dnie3r1Cwa14890Constants extends Dnie3Cwa14890Constants {
    private static final byte[] C_CV_CA = {127, 33, -127, -50, 95, 55, -127, -128, 60, -70, -36, 54, -124, -66, -13, 32, 65, -83, 21, 80, -119, 37, -115, -3, 32, -58, -111, 21, -41, 47, -100, 56, -86, -103, -83, 108, 26, -19, -6, -78, -65, -84, -112, -110, -4, 112, -52, -64, 12, -81, 72, 42, 75, -29, 26, -3, -67, 60, -68, -116, -125, -126, -49, 6, -68, 7, 25, -70, -85, -75, 107, 110, -56, 7, 96, -92, -87, 63, -94, -41, -61, 71, -13, 68, 39, -7, -1, 92, -115, -26, -42, 93, -84, -107, -14, -15, -99, -84, 0, 83, -33, 17, -91, 7, -5, 98, 94, -21, -115, -92, -64, 41, -98, 74, 33, 18, -85, 112, 71, 88, -117, -115, 109, -89, 89, 34, 20, -14, -37, -95, 64, -57, -47, 34, 87, -101, 95, 56, 61, 34, 83, -56, -71, -53, 91, -61, 84, 58, 85, 102, 11, -38, -128, -108, 106, -5, 5, 37, -24, -27, 88, 107, 78, 99, -24, -110, 65, 73, 120, 54, -40, -45, -85, 8, -116, -44, 76, 33, 77, 106, -56, 86, -30, -96, 7, -12, 79, -125, 116, 51, 55, 55, 26, -35, -114, 3, 0, 1, 0, 1, 66, 8, 101, 115, 82, 68, 73, 96, 0, 6};
    private static final byte[] CHR_C_CV_CA = {101, 115, 83, 68, 73, 96, 0, 6};

    Dnie3r1Cwa14890Constants() {
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
