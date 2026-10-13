package es.gob.afirma.signvalidation;

import es.gob.afirma.core.RuntimeConfigNeededException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/SignValider.class */
public abstract class SignValider {
    private boolean relaxed = false;

    public abstract List<SignValidity> validate(byte[] bArr) throws RuntimeConfigNeededException, IOException;

    public abstract List<SignValidity> validate(byte[] bArr, boolean z) throws RuntimeConfigNeededException, IOException;

    public abstract List<SignValidity> validate(byte[] bArr, Properties properties) throws RuntimeConfigNeededException, IOException;

    public void setRelaxed(boolean z) {
        this.relaxed = z;
    }

    public boolean isRelaxed() {
        return this.relaxed;
    }

    protected static List<SignValidity> checkLongStandingValiditySign(List<SignValidity> list) {
        ArrayList arrayList = new ArrayList(list);
        int i = -1;
        int i2 = -1;
        boolean z = false;
        for (int i3 = 0; i3 < list.size(); i3++) {
            if (SignValidity.SIGN_DETAIL_TYPE.KO.equals(list.get(i3).getValidity())) {
                if (SignValidity.VALIDITY_ERROR.CERTIFICATE_EXPIRED.equals(list.get(i3).getError())) {
                    i = i3;
                } else {
                    z = true;
                }
            } else if (SignValidity.SIGN_DETAIL_TYPE.UNKNOWN.equals(list.get(i3).getValidity()) && SignValidity.VALIDITY_ERROR.SIGN_PROFILE_NOT_CHECKED.equals(list.get(i3).getError())) {
                i2 = i3;
            }
        }
        if (!z && i != -1 && i2 != -1) {
            SignValidity signValidity = (SignValidity) arrayList.get(0);
            arrayList.set(0, list.get(i2));
            arrayList.set(i2, signValidity);
        }
        return arrayList;
    }

    protected static List<SignValidity> checkValidityKOPriority(List<SignValidity> list) {
        ArrayList arrayList = new ArrayList();
        for (SignValidity signValidity : list) {
            if (!arrayList.contains(signValidity)) {
                if (SignValidity.SIGN_DETAIL_TYPE.KO.equals(signValidity.getValidity())) {
                    arrayList.add(0, signValidity);
                } else {
                    arrayList.add(signValidity);
                }
            }
        }
        return arrayList;
    }
}
