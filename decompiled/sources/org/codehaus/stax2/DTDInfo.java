package org.codehaus.stax2;

import org.codehaus.stax2.validation.DTDValidationSchema;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/DTDInfo.class */
public interface DTDInfo {
    Object getProcessedDTD();

    String getDTDRootName();

    String getDTDSystemId();

    String getDTDPublicId();

    String getDTDInternalSubset();

    DTDValidationSchema getProcessedDTDSchema();
}
